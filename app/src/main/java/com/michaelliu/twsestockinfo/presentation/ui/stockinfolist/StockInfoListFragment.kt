package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.michaelliu.twsestockinfo.databinding.FragmentStockInfoListBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.michaelliu.twsestockinfo.domain.model.StockInfo
import com.michaelliu.twsestockinfo.presentation.ui.stockinfolist.adapter.StockInfoListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class StockInfoListFragment : Fragment() {

    private val viewModel: StockInfoListViewModel by viewModels()
    private var _binding: FragmentStockInfoListBinding? = null
    private val binding get() = _binding!!

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
        collectUiState()

        viewModel.loadStockInfoList()
    }

    private fun setupRecyclerView() {
        stockInfoListAdapter = StockInfoListAdapter { stockInfo ->
            Timber.d("onClickItem : ${stockInfo.code} ${stockInfo.name}")
            showStockInfoDetail(stockInfo)
        }
        binding.stockListRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stockInfoListAdapter
        }
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
                            binding.progressIndicator.visibility = View.GONE
                            binding.stockListRecyclerView.visibility = View.VISIBLE
                            binding.tvErrorMessage.visibility = View.GONE
                            val stockInfoList = uiState.data
                            stockInfoListAdapter.submitList(stockInfoList)
                        }
                        is UiState.Error -> {
                            binding.progressIndicator.visibility = View.GONE
                            binding.stockListRecyclerView.visibility = View.GONE
                            binding.tvErrorMessage.visibility = View.VISIBLE
                            val errorMessage = uiState.message
                            binding.tvErrorMessage.text = errorMessage
                        }
                    }
                }
            }
        }
    }

    private fun showStockInfoDetail(stockInfo: StockInfo) {
        val title = "${stockInfo.code} ${stockInfo.name}"
        val message = """
            本益比：${stockInfo.peRatio ?: "-"}
            殖利率(%)：${stockInfo.dividendYield ?: "-"}
            股價淨值比：${stockInfo.pbRatio ?: "-"}
        """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("確定") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}