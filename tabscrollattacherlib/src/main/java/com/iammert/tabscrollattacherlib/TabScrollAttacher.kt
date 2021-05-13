package com.iammert.tabscrollattacherlib

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout

public class TabScrollAttacher(
    private val tabLayout: TabLayout,
    private val recyclerView: RecyclerView,
    private val tabStartIndexOffsets: List<Int>,
    configuration: Configuration.() -> Unit = {}
) {

    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var config: Configuration

    private var attacherState = AttacherState.IDLE

    private var isAttached = false

    private var scrollListener = object : RecyclerView.OnScrollListener() {
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
    }

    private var tabSelectedListener = object : SimpleTabSelectedListener() {
        override fun onTabSelected(p0: TabLayout.Tab?) {
            super.onTabSelected(p0)
            if (attacherState != AttacherState.RECYCLERVIEW_SCROLLING) {
                attacherState = AttacherState.TAB_SELECTED
                val recyclerViewPosition = tabStartIndexOffsets[tabLayout.selectedTabPosition]
                recyclerView.scrollToPosition(recyclerViewPosition, config.scrollMethod)
            }
        }
    }

    init {
        require(recyclerView.layoutManager is LinearLayoutManager) { "Only LinearLayoutManager is supported." }
        layoutManager = recyclerView.layoutManager as LinearLayoutManager
        config = Configuration().apply(configuration)
    }

    public fun detach() {
        if (isAttached) {
            recyclerView.removeOnScrollListener(scrollListener)
            tabLayout.removeOnTabSelectedListener(tabSelectedListener)
            isAttached = false
        }
    }

    public fun attach() {
        if (isAttached.not()) {
            recyclerView.addOnScrollListener(scrollListener)
            tabLayout.addOnTabSelectedListener(tabSelectedListener)
            isAttached = true
        }
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

    public class Configuration {
        internal var scrollMethod: ScrollMethod = ScrollMethod.Smooth
            private set

        public fun scrollDirectly() {
            scrollMethod = ScrollMethod.Direct
        }

        public fun scrollSmoothly(limit: Int? = null) {
            scrollMethod = when (limit) {
                null -> ScrollMethod.Smooth
                else -> ScrollMethod.LimitedSmooth(limit)
            }
        }
    }

}