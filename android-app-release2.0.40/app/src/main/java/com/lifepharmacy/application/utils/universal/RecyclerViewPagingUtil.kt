package com.lifepharmacy.application.utils.universal

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewPagingUtil(
  val recyclerView: RecyclerView,
  val layoutManager: GridLayoutManager,
  val listener: RecyclerPagingListener
) : RecyclerView.OnScrollListener() {
  private var pastVisibleItems: Int = 0
  private var visibleItemCount: Int = 0
  private var totalItemCount: Int = 0
  private var previousTotal: Int = 0
  var isLoading = true
  var skip = 0
  var take = 30
  override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
    super.onScrollStateChanged(recyclerView, newState)
    visibleItemCount = layoutManager.childCount
    totalItemCount = layoutManager.itemCount
    pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPosition()
    val visibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
//    if (visibleItem > skip + 1 - take) {
//      onBottomHit()
//    }


    if (!recyclerView.canScrollVertically(1)) {
      onBottomHit()
    }
  }

  private fun onBottomHit() {
    if (!isLoading && previousTotal > 0) {
      listener.onNextPage(skip, take)
    }
//    if (isLoading) {
//      if (totalItemCount > previousTotal) {
//        isLoading = false
//        previousTotal = totalItemCount
//      }
//    }
//    if (!isLoading && totalItemCount - visibleItemCount <= pastVisibleItems + take) {
//      pastVisibleItems++
//      listener.onNextPage(skip,take)
//      isLoading = true
//    }
  }

  fun nextPageLoaded(size: Int) {
    if (skip == 0) {
      recyclerView.post { // Call smooth scroll
        recyclerView.scrollToPosition(0)
      }
    }
    skip += take
    previousTotal = size
    isLoading = false

  }
}