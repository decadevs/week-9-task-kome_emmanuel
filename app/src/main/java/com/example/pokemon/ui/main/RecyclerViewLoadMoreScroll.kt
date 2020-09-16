package com.example.pokemon.ui.main

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewLoadMoreScroll : RecyclerView.OnScrollListener {
    /**the visibleThreshold circular shows up when the user sees the 1st item from the end of our downloaded data.**/
    private var visibleThreshold = 1
    private lateinit var mOnLoadMoreListener: OnLoadMoreListener
    private var isLoading = false
    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private var mLayoutManager: RecyclerView.LayoutManager

    fun setLoaded() {
        isLoading = false
    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    constructor(layoutManager:LinearLayoutManager) {
        this.mLayoutManager = layoutManager
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy <= 0) return

        totalItemCount = mLayoutManager.itemCount

        lastVisibleItem = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()

        if (!isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
            mOnLoadMoreListener.onLoadMore()
            isLoading = true
        }

    }

    /**created an interface where we are calling to load more data into our RecyclerView (OnLoadMoreListener)**/
    interface OnLoadMoreListener {
        fun onLoadMore()
    }


}