package com.michaelliu.twsestockinfo.presentation.ui.stockinfolist.adapter

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(context: Context, dp: Float) : RecyclerView.ItemDecoration() {
    private val spaceSize: Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics
    ).toInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val layoutManager = parent.layoutManager

        if (layoutManager is GridLayoutManager) {
            if (position == 0 || position == 1) {
                outRect.top = spaceSize
            }
            if (position % 2 == 0) {
                outRect.left = spaceSize
                outRect.right = spaceSize / 2
            } else {
                outRect.left = spaceSize / 2
                outRect.right = spaceSize
            }
        } else if (layoutManager is LinearLayoutManager) {
            if (position == 0) {
                outRect.top = spaceSize
            }
            outRect.left = spaceSize
            outRect.right = spaceSize
        }

        outRect.bottom = spaceSize
    }
}