package com.lifepharmacy.application.ui.dashboard.navigation

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.lifepharmacy.application.R
import com.lifepharmacy.application.managers.AlertManager
import com.lifepharmacy.application.model.notifications.NotificationAction
import com.lifepharmacy.application.model.notifications.NotificationPayLoad
import com.lifepharmacy.application.ui.auth.AuthActivity
import com.lifepharmacy.application.ui.dashboard.viewmodel.DashboardViewModel
import com.lifepharmacy.application.ui.home.fragments.HomeLandingFragment
import com.lifepharmacy.application.ui.livechat.LiveChatActivity
import com.lifepharmacy.application.ui.orders.fragments.MainOrdersFragment
import com.lifepharmacy.application.ui.orders.fragments.OrderDetailFragment
import com.lifepharmacy.application.ui.productList.ProductListFragment
import com.lifepharmacy.application.ui.products.fragment.ProductFragment
import com.lifepharmacy.application.ui.rating.fragments.MainRatingFragment
import com.lifepharmacy.application.ui.webActivity.WebViewActivity
import com.lifepharmacy.application.utils.universal.IntentAction

/**
 * DashBoardActivity Navigation Will be done by this
 */
class DashboardActivityRouter(
  val navController: NavController,
  val activity: Activity,
  val viewModel: DashboardViewModel
) {


  fun navigateToDestination(notificationModel: NotificationPayLoad) {
    notificationModel?.let {
      when (it.action) {
        NotificationAction.ORDER -> {
          navController.navigate(
            R.id.nav_order_details,
            OrderDetailFragment.getOrderDetailBundle(it.value, "12345")
          )
        }
        NotificationAction.SUB_ORDER -> {
          navController.navigate(
            R.id.nav_order_details,
            OrderDetailFragment.getOrderDetailBundle(it.value, "12345")
          )
        }
        NotificationAction.RATE -> {
          navController.navigate(
            R.id.nav_rating,
            MainRatingFragment.getBundle(subOrderID = it.value)
          )
        }
        NotificationAction.PRODUCT_LISTING -> {
          navController.navigate(
            R.id.nav_product_listing,
            ProductListFragment.getProductListingBundle(
              id = it.key,
              type = it.value,
              title = it.title
            )
          )
        }
        NotificationAction.LANDING_PAGE -> {
          navController.navigate(
            R.id.homeLandingFragment, HomeLandingFragment.getLandingPageBundle(it.title, it.value)
          )
        }
        NotificationAction.PRODUCT -> {
          navController.navigate(
            R.id.nav_product,
            ProductFragment.getBundle(productID = it.value, 0)
          )
        }
        NotificationAction.VOUCHER -> {
          navController.navigate(R.id.nav_voucher)
        }
        NotificationAction.PRESCRIPTION_REQUEST -> {
          navController.navigate(R.id.nav_prescription)
        }
        NotificationAction.ORDERS -> {
          navController.navigate(R.id.nav_orders)
        }
        NotificationAction.PRESCRIPTION_REQUESTS -> {
          navController.navigate(R.id.nav_orders, MainOrdersFragment.getBundle(1))
        }
        NotificationAction.WALLET -> {
          navController.navigate(R.id.nav_wallet)
        }
        NotificationAction.CART -> {
          navController.navigate(R.id.nav_cart)
        }
        NotificationAction.ACCOUNT -> {
          navController.navigate(R.id.nav_profile)
        }
        NotificationAction.WEB -> {
          WebViewActivity.open(activity, it.value, it.title)
        }

        NotificationAction.LOGIN -> {
          AuthActivity.open(activity)
        }
        NotificationAction.LINK -> {
          try {
            IntentAction.openLink(it.value, activity)
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
        NotificationAction.NON -> {

        }

      }
    }
  }

  fun openInAppPopUp() {
    navController.navigate(R.id.inAppPopupDialog)
  }

  fun openChat() {
    if (viewModel.appManager.persistenceManager.isLoggedIn()) {
      LiveChatActivity.open(
        activity = activity,
        name = viewModel.appManager.persistenceManager.getLoggedInUser()?.name ?: "",
        email = viewModel.appManager.persistenceManager.getLoggedInUser()?.email ?: "",
        phone = viewModel.appManager.persistenceManager.getLoggedInUser()?.phone ?: "",
      )
    } else {
      AlertManager.showErrorMessage(activity, "Please Login First to chat ")
    }
  }

}