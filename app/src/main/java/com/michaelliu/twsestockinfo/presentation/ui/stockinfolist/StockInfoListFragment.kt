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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StockInfoListFragment : Fragment() {

    private val viewModel: StockInfoListViewModel by viewModels()
    private var _binding: FragmentStockInfoListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockInfoListBinding.inflate(inflater, container, false)

        setupRecyclerView()
        collectUiState()

        viewModel.loadStockInfoList()

        return binding.root
    }

    private fun setupRecyclerView() {

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}