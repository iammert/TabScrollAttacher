package com.iammert.tabscrollattacher.data

import android.content.Context
import com.iammert.tabscrollattacher.R

object DataFetcher {

    fun fetchData(context: Context): List<Category> {
        val tabs = context.applicationContext.resources.getStringArray(R.array.tabs)
        val categories = arrayListOf<Category>()
        tabs.forEachIndexed { index, s ->

            val items = arrayListOf<Item>()
            for (i in 0..((Math.random() * 20).toInt())) {
                items.add(Item(i))
            }

            categories.add(Category(categoryId = index, categoryName = s, itemList = items))
        }

        return categories
    }
}