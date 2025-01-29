package com.michaelliu.twsestockinfo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.michaelliu.twsestockinfo.presentation.ui.stocklist.StockInfoListViewModel
import com.michaelliu.twsestockinfo.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val stockInfoListViewModel: StockInfoListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeStockState()
        stockInfoListViewModel.fetchStockInfoList()
    }

    private fun observeStockState() {
        stockInfoListViewModel.stockState.observe(this) { state ->
            when (state) {
                is NetworkResult.Success -> {
                    Timber.e("Stock list: ${state.data}")
                }
                is NetworkResult.Failure -> {
                    Timber.e("Error: ${state.exception}")
                }
            }
        }
    }
}