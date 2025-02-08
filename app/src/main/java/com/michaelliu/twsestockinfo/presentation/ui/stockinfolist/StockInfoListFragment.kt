package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import com.michaelliu.twsestockinfo.databinding.FragmentStockInfoListBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.michaelliu.twsestockinfo.R
import com.michaelliu.twsestockinfo.domain.model.StockInfo
import com.michaelliu.twsestockinfo.presentation.ui.stockinfolist.adapter.SpacingItemDecoration
import com.michaelliu.twsestockinfo.presentation.ui.stockinfolist.adapter.StockInfoListAdapter
import com.michaelliu.twsestockinfo.utils.NetworkStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class StockInfoListFragment : Fragment() {

    private val viewModel: StockInfoListViewModel by viewModels()
    private var _binding: FragmentStockInfoListBinding? = null
    private val binding get() = _binding!!

    private var lastNetworkStatus: NetworkStatus = NetworkStatus.UnKnown
    private lateinit var stockInfoListAdapter: StockInfoListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockInfoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFabScrollToTop()
        collectUiState()
        collectNetworkStatus()

        viewModel.loadStockInfoList()
        requireActivity().addMenuProvider(stockInfoListMenuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        stockInfoListAdapter = StockInfoListAdapter { stockInfo ->
            Timber.d("onClickItem : ${stockInfo.code} ${stockInfo.name}")
            showStockInfoDetail(stockInfo)
        }
        binding.stockListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stockInfoListAdapter
            itemAnimator = null // 防止RecyclerView閃一下
        }
        binding.stockListRecyclerView.addItemDecoration(
            SpacingItemDecoration(requireContext(), 24f)
        )
    }

    private fun setupFabScrollToTop() {
        binding.fabScrollToTop.setOnClickListener {
            binding.stockListRecyclerView.smoothScrollToPosition(0)
        }
        binding.stockListRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (firstVisibleItemPosition > 2) {
                    binding.fabScrollToTop.show()
                } else {
                    binding.fabScrollToTop.hide()
                }
            }
        })
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UiState.Loading -> {
                            binding.progressIndicator.visibility = View.VISIBLE
                            binding.stockListRecyclerView.visibility = View.GONE
                            binding.tvErrorMessage.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            // 因為台股商品數量太大 若直接submit排序過後list 會造成DiffUtil計算負擔
                            // 所以因應這個問題 這邊直接submit null 強制清空list 不讓DiffUtil做多餘計算
                            stockInfoListAdapter.submitList(null)

                            val stockInfoList = uiState.data
                            stockInfoListAdapter.submitList(stockInfoList) {
                                binding.stockListRecyclerView.scrollToPosition(0)
                            }

                            binding.progressIndicator.visibility = View.GONE
                            binding.stockListRecyclerView.visibility = View.VISIBLE
                            binding.tvErrorMessage.visibility = View.GONE
                        }
                        is UiState.Error -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.stockListRecyclerView.visibility = View.GONE
                            binding.tvErrorMessage.text = getString(R.string.no_data_available)
                            binding.tvErrorMessage.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun collectNetworkStatus() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.networkStatus.collect { networkStatus ->
                    when (networkStatus) {
                        is NetworkStatus.UnKnown -> {
                            binding.tvNetworkStatusBar.visibility = View.GONE
                        }
                        is NetworkStatus.Available -> {
                            if (lastNetworkStatus is NetworkStatus.Unavailable) {
                                binding.tvNetworkStatusBar.post {
                                    showStatusBarWithAnimation(
                                        barText = getString(R.string.network_reconnected),
                                        isSuccess = true
                                    )
                                }

                                lifecycleScope.launch {
                                    delay(1000L)
                                    if (viewModel.networkStatus.value is NetworkStatus.Available) {
                                        hideStatusBarWithAnimation()
                                        viewModel.loadStockInfoList()
                                    }
                                }
                            }
                        }
                        is NetworkStatus.Unavailable -> {
                            binding.tvNetworkStatusBar.post {
                                showStatusBarWithAnimation(
                                    barText = getString(R.string.network_disconnected),
                                    isSuccess = false
                                )
                            }
                        }
                    }

                    lastNetworkStatus = networkStatus
                }
            }
        }
    }

    private fun showStockInfoDetail(stockInfo: StockInfo) {
        val title = "${stockInfo.code} ${stockInfo.name}"
        val message = """
            ${getString(R.string.pe_ratio)}：${stockInfo.peRatio ?: "-"}
            ${getString(R.string.dividend_yield)}：${stockInfo.dividendYield ?: "-"}
            ${getString(R.string.pb_ratio)}：${stockInfo.pbRatio ?: "-"}
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showSortTypeBottomSheet() {
        val bottomSheet = SortTypeBottomSheet(
            viewModel.getCurrentSortType()
        ) { selectedType ->
            viewModel.sortStockList(selectedType)
        }
        bottomSheet.show(parentFragmentManager, "SortTypeBottomSheet")
    }

    private fun showStatusBarWithAnimation(barText: String, isSuccess: Boolean) {
        binding.tvNetworkStatusBar.apply {
            text = barText
            if (isSuccess) {
                setTextColor(getColor(requireContext(), R.color.status_bar_success_text_color))
                setBackgroundColor(getColor(requireContext(), R.color.status_bar_success_background_color))
            } else {
                setTextColor(getColor(requireContext(), R.color.status_bar_error_text_color))
                setBackgroundColor(getColor(requireContext(), R.color.status_bar_error_background_color))
            }
            visibility = View.VISIBLE
            translationY = -height.toFloat()
            animate()
                .translationY(0f)
                .setDuration(300)
                .start()
        }
    }

    private fun hideStatusBarWithAnimation() {
        binding.tvNetworkStatusBar.animate()
            .translationY(-binding.tvNetworkStatusBar.height.toFloat())
            .setDuration(300)
            .withEndAction {
                binding.tvNetworkStatusBar.visibility = View.GONE
            }
            .start()
    }

    private val stockInfoListMenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.menu_stock_info_list, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.action_filter -> {
                    showSortTypeBottomSheet()
                    true
                }
                else -> false
            }
        }
    }

}