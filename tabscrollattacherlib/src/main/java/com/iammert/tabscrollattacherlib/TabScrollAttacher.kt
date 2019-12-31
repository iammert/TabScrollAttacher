package com.iammert.tabscrollattacherlib

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

class TabScrollAttacher(
    private val tabLayout: TabLayout,
    private val recyclerView: RecyclerView,
    private val tabStartIndexOffsets: List<Int>
) {

    private val layoutManager: LinearLayoutManager

    private var attacherState = AttacherState.IDLE

    init {
        require(recyclerView.layoutManager is LinearLayoutManager) { "Only LinearLayoutManager is supported." }
        layoutManager = recyclerView.layoutManager as LinearLayoutManager
        observeRecyclerViewScroll()
        observeTabLayoutSelection()
    }

    private fun observeRecyclerViewScroll() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (attacherState == AttacherState.TAB_SELECTED) {
                    return
                }

                val isScrolling = dx != 0 || dy != 0
                if (attacherState == AttacherState.IDLE && isScrolling) {
                    attacherState = AttacherState.RECYCLERVIEW_SCROLLING
                }

                val calculatedRecyclerViewItemPosition = when {
                    layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount - 1 -> layoutManager.findLastVisibleItemPosition()
                    layoutManager.findFirstVisibleItemPosition() == 0 -> layoutManager.findFirstVisibleItemPosition()
                    else -> findMidVisibleRecyclerItemPosition()
                }

                val tabIndex = getTabIndex(calculatedRecyclerViewItemPosition)
                if (tabIndex != tabLayout.selectedTabPosition) {
                    tabLayout.getTabAt(tabIndex)?.select()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    attacherState = AttacherState.IDLE
                }
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun observeTabLayoutSelection() {
        tabLayout.addOnTabSelectedListener(object : SimpleTabSelectedListener() {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                super.onTabSelected(p0)
                if (attacherState != AttacherState.RECYCLERVIEW_SCROLLING) {
                    attacherState = AttacherState.TAB_SELECTED
                    val recyclerViewPosition = tabStartIndexOffsets[tabLayout.selectedTabPosition]
                    recyclerView.smoothScrollToPosition(recyclerViewPosition)
                }
            }
        })
    }

    private fun findMidVisibleRecyclerItemPosition(): Int {
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        return (firstVisibleItemPosition + lastVisibleItemPosition) / 2
    }

    private fun getTabIndex(recyclerItemPosition: Int): Int {
        var calculatedTabIndex = -1
        tabStartIndexOffsets.forEachIndexed { tabIndex, startOffset ->
            if (recyclerItemPosition >= startOffset) {
                calculatedTabIndex = tabIndex
            }
        }
        return calculatedTabIndex
    }

}