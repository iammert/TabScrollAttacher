package com.iammert.tabscrollattacher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iammert.tabscrollattacher.data.Category
import com.iammert.tabscrollattacher.data.DataFetcher
import com.iammert.tabscrollattacher.data.Item
import com.iammert.tabscrollattacher.databinding.ActivityMainBinding
import com.iammert.tabscrollattacherlib.TabScrollAttacher

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categories = DataFetcher.fetchData(applicationContext)

        /**
         * load recyclerview adapter
         */
        val adapter = ItemListAdapter()
        adapter.setItems(getAllItems(categories))
        binding.recyclerView.adapter = adapter

        /**
         * Load tabs
         */
        TabLoader.loadTabs(binding.tabLayout, categories)

        /**
         * SETUP ATTACHER
         */
        val indexOffsets = getCategoryIndexOffsets(categories)
        val attacher = TabScrollAttacher(binding.tabLayout, binding.recyclerView, indexOffsets) {
            scrollSmoothly()
        }

        attacher.attach()

        //attacher.detach()

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
