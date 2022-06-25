package com.lifepharmacy.application.network

import android.content.Context
import android.util.Log
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
  private val context: Context) : Authenticator {
  companion object {
    private val TAG = TokenAuthenticator::class.java.simpleName
  }

  override fun authenticate(route: Route?,
    response: Response): Request? {
    Log.d(TAG, "initializing")

    synchronized(this) {
    }
    return response.request
      .newBuilder()
      .header("", "")
      .build()
  }
}
