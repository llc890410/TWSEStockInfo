package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelliu.twsestockinfo.databinding.ItemStockInfoBinding
import com.michaelliu.twsestockinfo.domain.model.StockInfo

class StockInfoListAdapter(
    private val onItemClicked: (StockInfo) -> Unit
) : ListAdapter<StockInfo, StockInfoListAdapter.StockInfoViewHolder>(StockInfoDiffCallback()) {

    private val fastClickInterval = 500L
    private var lastClickTime = 0L

    inner class StockInfoViewHolder(
        private val binding: ItemStockInfoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(stockInfo: StockInfo) {
            // 股票代號、名稱
            binding.tvStockCode.text = stockInfo.code
            binding.tvStockName.text = stockInfo.name

            // 開盤、收盤
            binding.tvOpeningPrice.text = stockInfo.openingPrice?.toString() ?: "-"
            binding.tvClosingPrice.text = stockInfo.closingPrice?.toString() ?: "-"

            // 最高、最低
            binding.tvHighestPrice.text = stockInfo.highestPrice?.toString() ?: "-"
            binding.tvLowestPrice.text = stockInfo.lowestPrice?.toString() ?: "-"

            // 漲跌、月平均
            binding.tvChange.text = stockInfo.change?.toString() ?: "-"
            binding.tvMonthlyAvgPrice.text = stockInfo.monthlyAvgPrice?.toString() ?: "-"

            // 成交比數、成交股數、成交金額
            binding.tvTransaction.text = stockInfo.transaction?.toString() ?: "-"
            binding.tvTradeVolume.text = stockInfo.tradeVolume?.toString() ?: "-"
            binding.tvTradeValue.text = stockInfo.tradeValue?.toString() ?: "-"

            // 收盤 > 月平均 => 紅; 收盤 < 月平均 => 綠
            val closingPrice = stockInfo.closingPrice ?: 0.0
            val monthlyAvgPrice = stockInfo.monthlyAvgPrice ?: 0.0
            binding.tvClosingPrice.setTextColor(
                if (closingPrice > monthlyAvgPrice) Color.RED
                else if (closingPrice < monthlyAvgPrice) Color.GREEN
                else Color.BLACK
            )

            // 漲跌價差 > 0 => 紅; 漲跌價差 < 0 => 綠
            val change = stockInfo.change ?: 0.0
            binding.tvChange.setTextColor(
                if (change > 0) Color.RED
                else if (change < 0) Color.GREEN
                else Color.BLACK
            )

            binding.root.setOnClickListener {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < fastClickInterval) {
                    return@setOnClickListener
                }
                lastClickTime = currentTime

                onItemClicked(stockInfo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockInfoViewHolder {
        val binding = ItemStockInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockInfoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class StockInfoDiffCallback : DiffUtil.ItemCallback<StockInfo>() {
    override fun areItemsTheSame(oldItem: StockInfo, newItem: StockInfo): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: StockInfo, newItem: StockInfo): Boolean {
        if (oldItem.code != newItem.code) {
            return false
        }
        return oldItem == newItem
    }
}
