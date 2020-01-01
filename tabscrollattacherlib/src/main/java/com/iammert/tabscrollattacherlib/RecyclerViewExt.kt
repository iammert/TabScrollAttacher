package com.iammert.tabscrollattacherlib

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal fun RecyclerView.scrollToPosition(position: Int, scrollMethod: ScrollMethod) {
    when (scrollMethod) {
        is ScrollMethod.Direct -> scrollToPosition(position)
        is ScrollMethod.Smooth -> smoothScrollToPosition(position)
        is ScrollMethod.LimitedSmooth -> smoothScrollToPosition(position, scrollMethod.limit)
    }
}

private fun RecyclerView.smoothScrollToPosition(position: Int, scrollLimit: Int) {
    layoutManager?.apply {
        when (this) {
            is LinearLayoutManager -> {
                val topItem = findFirstVisibleItemPosition()
                val distance = topItem - position
                val anchorItem = when {
                    distance > scrollLimit -> position + scrollLimit
                    distance < -scrollLimit -> position - scrollLimit
                    else -> topItem
                }
                if (anchorItem != topItem) scrollToPosition(anchorItem)
                post {
                    smoothScrollToPosition(position)
                }
            }
            else -> smoothScrollToPosition(position)
        }
    }
}