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
        private const val VIEW_TYPE_SHIMMER = 0
        private const val VIEW_TYPE_ITEM = 1
        private const val SHIMMER_ITEM_COUNT = 10
        private const val FAST_CLICK_INTERVAL = 500L
    }

    private var isShimmer = false

    @SuppressLint("NotifyDataSetChanged")
    fun showShimmer(show: Boolean) {
        isShimmer = show
        notifyDataSetChanged()
    }

    inner class StockInfoViewHolder(
        private val binding: ItemStockInfoBinding
    ) : ViewHolder(binding.root) {
        private var lastClickTime = 0L

        fun bind(stockInfo: StockInfo) = with(binding) {
            tvStockCode.text = stockInfo.code
            tvStockName.text = stockInfo.name
            tvOpeningPrice.text = stockInfo.openingPrice?.toString() ?: "-"
            tvClosingPrice.text = stockInfo.closingPrice?.toString() ?: "-"
            tvHighestPrice.text = stockInfo.highestPrice?.toString() ?: "-"
            tvLowestPrice.text = stockInfo.lowestPrice?.toString() ?: "-"
            tvChange.text = stockInfo.change?.toString() ?: "-"
            tvMonthlyAvgPrice.text = stockInfo.monthlyAvgPrice?.toString() ?: "-"
            tvTransaction.text = stockInfo.transaction?.toString() ?: "-"
            tvTradeVolume.text = stockInfo.tradeVolume?.toString() ?: "-"
            tvTradeValue.text = stockInfo.tradeValue?.toString() ?: "-"

            tvClosingPrice.setTextColor(getClosingPriceColor(stockInfo))
            tvChange.setTextColor(getChangeColor(stockInfo))

            root.setOnClickListener {
                if (System.currentTimeMillis() - lastClickTime >= FAST_CLICK_INTERVAL) {
                    lastClickTime = System.currentTimeMillis()
                    onItemClicked(stockInfo)
                }
            }
        }

        private fun getClosingPriceColor(stockInfo: StockInfo) = getColor(
            // 收盤 > 月平均 => 紅; 收盤 < 月平均 => 綠
            binding.root.context, when {
                (stockInfo.closingPrice ?: 0.0) > (stockInfo.monthlyAvgPrice ?: 0.0) ->
                    R.color.list_item_text_color_red
                (stockInfo.closingPrice ?: 0.0) < (stockInfo.monthlyAvgPrice ?: 0.0) ->
                    R.color.list_item_text_color_green
                else -> R.color.list_item_text_color
            }
        )

        private fun getChangeColor(stockInfo: StockInfo) = getColor(
            // 漲跌價差 > 0 => 紅; 漲跌價差 < 0 => 綠
            binding.root.context, when {
                (stockInfo.change ?: 0.0) > 0 -> R.color.list_item_text_color_red
                (stockInfo.change ?: 0.0) < 0 -> R.color.list_item_text_color_green
                else -> R.color.list_item_text_color
            }
        )
    }

    inner class ShimmerViewHolder(binding: ItemStockInfoShimmerBinding) : ViewHolder(binding.root)

    override fun getItemViewType(position: Int) = if (isShimmer) VIEW_TYPE_SHIMMER else VIEW_TYPE_ITEM

    override fun getItemCount() = if (isShimmer) SHIMMER_ITEM_COUNT else super.getItemCount()

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
    override fun areItemsTheSame(oldItem: StockInfo, newItem: StockInfo) = oldItem.code == newItem.code
    override fun areContentsTheSame(oldItem: StockInfo, newItem: StockInfo) = oldItem == newItem
}
