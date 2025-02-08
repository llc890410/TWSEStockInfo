package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist.adapter

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(context: Context, dp: Float) : RecyclerView.ItemDecoration() {
    private val spaceSize: Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
    ).toInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position == 0) {
            outRect.top = spaceSize
        }
        outRect.bottom = spaceSize
    }
}