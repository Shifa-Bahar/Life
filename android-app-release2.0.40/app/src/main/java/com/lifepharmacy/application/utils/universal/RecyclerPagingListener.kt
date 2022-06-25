package com.lifepharmacy.application.utils.universal

interface
RecyclerPagingListener {
  fun onNextPage(skip:Int,take:Int)
}