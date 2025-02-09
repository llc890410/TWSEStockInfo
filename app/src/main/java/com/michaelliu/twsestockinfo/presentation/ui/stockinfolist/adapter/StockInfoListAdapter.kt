package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.michaelliu.twsestockinfo.R
import com.michaelliu.twsestockinfo.databinding.ItemStockInfoBinding
import com.michaelliu.twsestockinfo.databinding.ItemStockInfoShimmerBinding
import com.michaelliu.twsestockinfo.domain.model.StockInfo

class StockInfoListAdapter(
    private val onItemClicked: (StockInfo) -> Unit
) : ListAdapter<StockInfo, ViewHolder>(StockInfoDiffCallback()) {

    companion object {
        private const val FAST_CLICK_INTERVAL = 500L

        private const val VIEW_TYPE_SHIMMER = 0
        private const val VIEW_TYPE_ITEM = 1

        private const val SHIMMER_ITEM_COUNT = 10
    }

    private var isShimmer = false
    private var lastClickTime = 0L

    @SuppressLint("NotifyDataSetChanged")
    fun showShimmer(show: Boolean) {
        isShimmer = show
        notifyDataSetChanged()
    }

    inner class StockInfoViewHolder(
        private val binding: ItemStockInfoBinding
    ) : ViewHolder(binding.root) {
        fun bind(stockInfo: StockInfo) {
            val context = binding.root.context

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
                if (closingPrice > monthlyAvgPrice) getColor(context, R.color.list_item_text_color_red)
                else if (closingPrice < monthlyAvgPrice) getColor(context, R.color.list_item_text_color_green)
                else getColor(context, R.color.list_item_text_color)
            )

            // 漲跌價差 > 0 => 紅; 漲跌價差 < 0 => 綠
            val change = stockInfo.change ?: 0.0
            binding.tvChange.setTextColor(
                if (change > 0) getColor(context, R.color.list_item_text_color_red)
                else if (change < 0) getColor(context, R.color.list_item_text_color_green)
                else getColor(context, R.color.list_item_text_color)
            )

            binding.root.setOnClickListener {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastClickTime < FAST_CLICK_INTERVAL) {
                    return@setOnClickListener
                }
                lastClickTime = currentTime

                onItemClicked(stockInfo)
            }
        }
    }

    inner class ShimmerViewHolder(
        binding: ItemStockInfoShimmerBinding
    ) : ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (isShimmer) VIEW_TYPE_SHIMMER else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return if (isShimmer) {
            SHIMMER_ITEM_COUNT
        } else {
            super.getItemCount()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SHIMMER -> {
                val binding = ItemStockInfoShimmerBinding.inflate(inflater, parent, false)
                ShimmerViewHolder(binding)
            }
            else -> {
                val binding = ItemStockInfoBinding.inflate(inflater, parent, false)
                StockInfoViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is StockInfoViewHolder) {
            val item = getItem(position)
            holder.bind(item)
        }
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
