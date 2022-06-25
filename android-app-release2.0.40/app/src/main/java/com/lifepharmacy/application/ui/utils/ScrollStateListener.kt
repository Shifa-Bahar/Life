package com.lifepharmacy.application.ui.utils

import android.os.Build.VERSION_CODES
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.widget.NestedScrollView
import com.lifepharmacy.application.utils.universal.Logger


/**
 * Created by Zahid Ali
 */
@RequiresApi(VERSION_CODES.M)
class ScrollStateListener(
    private val scrollState: AppScrollState,
    val scrollView: NestedScrollView? = null
) : View.OnScrollChangeListener {
    var previousScrollDirection = 0
    override fun onScrollChange(p0: View?, p1: Int, scrollY: Int, p3: Int, oldScrollY: Int) {
      scrollState.setScrollDirection(0)
        try {
            if (previousScrollDirection == 0 || previousScrollDirection == 2) {
                if (scrollY > oldScrollY) {
                    if ((scrollY - oldScrollY) >= 50) {
                        scrollState.setScrollDirection(1)
                        previousScrollDirection = 1
                    }
                }
            }
            if (previousScrollDirection == 0 || previousScrollDirection == 1) {
                if (scrollY < oldScrollY) {
                    if ((oldScrollY - scrollY) >= 50) {
                        scrollState.setScrollDirection(2)
                        previousScrollDirection = 2
                    }
                }
            }
            // Calculate the scrolldiff

            // Calculate the scrolldiff
            p0?.let {
                scrollView?.let {
                    val view = scrollView.getChildAt(scrollView.childCount - 1)
                    val diff: Int = view.bottom - (scrollView.height + scrollView.scrollY)
                    Logger.d("diff", diff.toString())
                    Logger.d("diffBottom",view.bottom.toString())
                    if (diff == 0) {
                        scrollState.setScrollDirection(3)
                        Logger.d("diff", "HitBottom")
                    }
                }

            }


//            if (scrollY == oldScrollY) {
//                Logger.d("hitBottom", "HitBottom")
//                scrollState.setScrollDirection(3)
//            }
        } catch (e: Exception) {

        }

    }
}