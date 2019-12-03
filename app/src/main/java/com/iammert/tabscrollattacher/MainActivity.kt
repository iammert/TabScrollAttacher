package com.iammert.tabscrollattacher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.iammert.tabscrollattacher.data.Category
import com.iammert.tabscrollattacher.data.DataFetcher
import com.iammert.tabscrollattacher.data.Item
import com.iammert.tabscrollattacherlib.TabScrollAttacher
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val categories = DataFetcher.fetchData(applicationContext)

        /**
         * load recyclerview adapter
         */
        val adapter = ItemListAdapter()
        adapter.setItems(getAllItems(categories))
        recyclerView.adapter = adapter

        /**
         * Load tabs
         */
        TabLoader.loadTabs(tabLayout, categories)

        /**
         * SETUP ATTACHER
         */
        val indexOffsets = getCategoryIndexOffsets(categories)
        TabScrollAttacher(tabLayout, recyclerView, indexOffsets)
    }

    /**
     * Calculate your index offset list.
     * Attacher will talk to recyclerview and tablayout
     * with offsets and indexes.
     */
    private fun getCategoryIndexOffsets(categories: List<Category>): List<Int> {
        val indexOffsetList = arrayListOf<Int>()
        categories.forEach { categoryItem ->
            if (indexOffsetList.isEmpty()) {
                indexOffsetList.add(0)
            } else {
                indexOffsetList.add(indexOffsetList.last() + categoryItem.itemList.size)
            }
        }
        return indexOffsetList
    }

    private fun getAllItems(categories: List<Category>): List<Item> {
        val items = arrayListOf<Item>()
        categories.forEach { items.addAll(it.itemList) }
        return items
    }
}
