package com.lifepharmacy.application.ui.dashboard

import android.animation.Animator
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.RemoteMessage
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseActivity
import com.lifepharmacy.application.databinding.ActivityDashboardWithNativeBottomBinding
import com.lifepharmacy.application.enums.PopupClicked
import com.lifepharmacy.application.model.notifications.*
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.dashboard.viewmodel.DashboardViewModel
import com.lifepharmacy.application.ui.home.HomeNavigation
import com.lifepharmacy.application.utils.universal.ConstantsUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import com.lifepharmacy.application.BuildConfig
import com.lifepharmacy.application.ui.UpdateAppActivity
import com.lifepharmacy.application.ui.dashboard.adapter.ClickStickyMessage
import com.lifepharmacy.application.ui.dashboard.adapter.StickyMessageAdapter
import com.lifepharmacy.application.ui.dashboard.dailog.ReviewAsking
import com.lifepharmacy.application.ui.dashboard.navigation.DashboardActivityRouter
import com.lifepharmacy.application.ui.documents.dialog.DocTypSelectionBottomSheet.Companion.TAG
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDouble
import com.lifepharmacy.application.utils.universal.Logger
import com.lifepharmacy.application.utils.universal.SnapToBlock
import com.livechatinc.inappchat.ChatWindowConfiguration
import com.livechatinc.inappchat.ChatWindowErrorType
import com.livechatinc.inappchat.ChatWindowView
import com.livechatinc.inappchat.models.NewMessageModel
import com.pusher.pushnotifications.PushNotificationReceivedListener
import com.pusher.pushnotifications.PushNotifications


@AndroidEntryPoint
class DashboardWithNativeBottomActivity : BaseActivity<ActivityDashboardWithNativeBottomBinding>(),
  ClickInstantOrderFloating, ClickStickyMessage {
  private val viewModel: DashboardViewModel by viewModels()
  private val viewModelAddress: AddressViewModel by viewModels()
  private val viewModelCart: CartViewModel by viewModels()
  private lateinit var navController: NavController
  private lateinit var dashboardRouter: DashboardActivityRouter
  private lateinit var askingForReview: ReviewAsking

  //  lateinit var list: ArrayList<BottomBarModel>
  var broadcastReceiver: BroadcastReceiver? = null
  var navBroadcastReceiver: BroadcastReceiver? = null
  var debouncePeriod: Long = 750

  private val coroutineScope = lifecycle.coroutineScope

  private var navigationJob: Job? = null
  private var getUserMessages: Job? = null
  var isInAppPopUpOpened: Boolean = false
  var isDashBoardInView: Boolean = false

//  var reviewManager: ReviewManager? = null
//  var reviewInfo: ReviewInfo? = null

  lateinit var stickyMessageAdapter: StickyMessageAdapter


  lateinit var chatWindowView: ChatWindowView
  lateinit var configuration: ChatWindowConfiguration

  companion object {
    fun open(activity: Activity) {
      val intent = Intent(activity, DashboardWithNativeBottomActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      activity.startActivity(intent)
    }

    fun openToScreen(activity: Context, notificationModel: NotificationPayLoad) {
      val intent = Intent(activity, DashboardWithNativeBottomActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      val bundle = Bundle()
      bundle.putParcelable("payload", notificationModel)
      intent.putExtras(bundle)
      activity.startActivity(intent)
    }

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (BuildConfig.VERSION_CODE.intToNullSafeDouble() < viewModel.appManager.storageManagers.config.minimumAndroidVersion ?: 1.0 && BuildConfig.BUILD_TYPE != "dev") {
      UpdateAppActivity.open(this)
    }
    if (viewModel.appManager.persistenceManager.isThereCart()) {
      updateCartFromServer()
    }
    navController = Navigation.findNavController(this, R.id.fragment)
    navController.addOnDestinationChangedListener { controller, destination, arguments ->
      isInAppPopUpOpened = destination.id == R.id.inAppPopupDialog
      viewModel.checkIfItsMainView(destination.id)
    }

    dashboardRouter =
      DashboardActivityRouter(navController, activity = this, viewModel = viewModel)
    askingForReview = ReviewAsking(context = this, viewModel)

    initBottomNav()
    initUI()
    viewModel.appManager.storageManagers.getSettings()
    binding.button18.setOnClickListener {
      dashboardRouter.openChat()
    }
//    getReviewInformation()

    getUserMessages()

    PushNotifications.setOnMessageReceivedListenerForVisibleActivity(this, object :
      PushNotificationReceivedListener {
      override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Logger.d("PusherBeamMessage", remoteMessage.toString())
      }
    })

  }


  private fun initChat() {
//    val customParamsMap = HashMap<String, String>()
//    customParamsMap["phone"] = appManager.persistenceManager.getLoggedInUser()?.phone.toString()
//    customParamsMap["source"] = "profile"
    val configuration = ChatWindowConfiguration(
      ConstantsUtil.LIVE_CHAT_LICENSE,
      ConstantsUtil.CHAT_WINDOW_GROUP,
      appManager.persistenceManager.getLoggedInUser()?.name,
      appManager.persistenceManager.getLoggedInUser()?.email,
      null
    )
    chatWindowView =
      ChatWindowView.createAndAttachChatWindowInstance(this@DashboardWithNativeBottomActivity)
    chatWindowView.setUpWindow(configuration)
    chatWindowView.setUpListener(object : ChatWindowView.ChatWindowEventsListener {
      override fun onChatWindowVisibilityChanged(visible: Boolean) {

      }

      override fun onNewMessage(message: NewMessageModel?, windowVisible: Boolean) {
        if (isDashBoardInView && message != null && !windowVisible && message.author.name != viewModel.appManager.persistenceManager.getLoggedInUser()?.name) {
          viewModel.messageReceived()
        }
      }

      override fun onStartFilePickerActivity(intent: Intent?, requestCode: Int) {
      }

      override fun onError(
        errorType: ChatWindowErrorType?,
        errorCode: Int,
        errorDescription: String?
      ): Boolean {
        return true
      }

      override fun handleUri(uri: Uri?): Boolean {
        return false
      }

    })
    chatWindowView.initialize()
    viewModel.isThereSupportMessage.value = false
  }


  private fun initStickyMessages() {
    stickyMessageAdapter = StickyMessageAdapter(this, this)
    binding.llSticky.rvMessages.adapter = stickyMessageAdapter
    val snapToBlock = SnapToBlock(1)
    snapToBlock.attachToRecyclerView(binding.llSticky.rvMessages)
    binding.llSticky.rvMessages.onFlingListener = null;
    snapToBlock.attachToRecyclerView(binding.llSticky.rvMessages)
    snapToBlock.setSnapBlockCallback(object : SnapToBlock.SnapBlockCallback {
      override fun onBlockSnap(snapPosition: Int) {
      }

      override fun onBlockSnapped(snapPosition: Int) {
        binding.llSticky.pageIndicatorView.selection = snapPosition;
      }

    })
  }


  override fun onResume() {
    super.onResume()
    viewModel.appManager.persistenceManager.setState(true)
    viewModel.appManager.pusherManager.startChannelListener()
    observerNewRidBroadCast()
    observerHomeNavBroadCast()
    iniNavigation()
    observer()
    isDashBoardInView = true
  }

  private fun iniNavigation() {
    val homeNavigation = HomeNavigation(this@DashboardWithNativeBottomActivity)
    CoroutineScope(Dispatchers.Main.immediate).launch {
      delay(500)
      if (intent.hasExtra("payload")) {
        val data: NotificationPayLoad? = intent.getParcelableExtra("payload")
        homeNavigation.triggerNavigation(
          key = data?.value ?: "",
          value = data?.key ?: "",
          heading = data?.title ?: ""
        )
      }
    }
  }

  override fun onPause() {
    viewModel.appManager.persistenceManager.setState(false)
    isDashBoardInView = false
    super.onPause()

  }

  override fun onDestroy() {
    try {
      unregisterReceiver(broadcastReceiver)
      unregisterReceiver(navBroadcastReceiver)
      viewModel.appManager.persistenceManager.setState(false)
      viewModel.appManager.persistenceManager.saveCompianID("")
    } catch (e: java.lang.Exception) {
      e.printStackTrace()
    }
    super.onDestroy()
  }

  fun initUI() {
    binding.viemodel = viewModel
    binding.click = this
    binding.lifecycleOwner = this
    binding.llInstant.viewModel = viewModel
    binding.llInstant.click = this
    binding.llInstant.lifecycleOwner = this
    binding.llInstant.animation.setAnimation("bike.json")
    binding.llInstant.animation.repeatCount = -1
    binding.llInstant.animation.playAnimation()
    binding.llSticky.viewModel = viewModel
    binding.llSticky.lifecycleOwner = this
    initStickyMessages()
  }


  private fun observer() {
    viewModel.popupClicked.observe(this, Observer {
      it?.let { it ->
        when (it) {
          PopupClicked.CLICKED -> {
            viewModel.popupClicked.value = PopupClicked.NON
          }
          PopupClicked.CLOSE -> {
            isInAppPopUpOpened = false
            navigateToInAppPopup()
            viewModel.popupClicked.value = PopupClicked.NON

          }
          PopupClicked.NON -> {

          }
        }
      }
    })
    viewModel.appManager.storageManagers.chatInitiatedMut.observe(this) {
      it?.let {
        if (it) {
          initChat()
        }
      }
    }

    viewModel.isMainView.observe(this) {
      if (it) {
        getUserMessages?.cancel()
        getUserMessages = CoroutineScope(Dispatchers.Main.immediate).launch {
          delay(3000)
//          getUserMessages()
        }
      }
    }


    viewModel.inAppSticky.observe(this) {
      it?.let {
        if (it.isNullOrEmpty()) {
          viewModel.showStickyMessages.value = false
        } else {
          viewModel.showStickyMessages.value = true
          stickyMessageAdapter.setDataChanged(it)
          binding.llSticky.pageIndicatorView.count = it.size
        }
      }
    }

    viewModel.offersManagers.cartQtyCountMut.observe(this, Observer {
      it?.let {
        if (it > 0) {
          val badge = binding.bottomNavigation.getOrCreateBadge(R.id.nav_cart)
          badge.isVisible = true
          badge.number = it
        } else {
          val badge = binding.bottomNavigation.getOrCreateBadge(R.id.nav_cart)
          badge.isVisible = false
        }
      }
    })
    viewModel.appManager.loadingState.getAnimationState().observe(this, Observer {
      it?.let {
        if (it) {
          showAnimations()
        } else {
          hideAnimations()
        }
      }
    })
    viewModelAddress.deliveredAddressMut.observe(this, Observer {
      it?.let {

      }
    })
    viewModel.notificationPayLoad.observe(this) {
      it?.let {
        navigationJob?.cancel()
        navigationJob = coroutineScope.launch {
          delay(debouncePeriod)
          dashboardRouter.navigateToDestination(it)
        }
      }
    }
    ConstantsUtil.notificationPayLoad.observe(this) {
      it?.let {
        navigationJob?.cancel()
        navigationJob = coroutineScope.launch {
          delay(debouncePeriod)
          dashboardRouter.navigateToDestination(it)
        }
      }
    }
    viewModel.openReviewBox.observe(this) {
      it?.let {
        if (it) {
          CoroutineScope(Dispatchers.Main.immediate).launch {
            viewModel.openReviewBox.value = false
            delay(100)
            if (!viewModel.appManager.persistenceManager.isReviewBoxOpened()) {
              askingForReview.askForReview()
            }

          }
        }
      }
    }
  }

  private fun updateCartFromServer() {
    viewModel.getCartDetails().observe(this, {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.data?.items?.let { it1 ->
              viewModel.offersManagers.updateCartFromServer(
                this,
                it1,
                it.data.data
              )
            }
          }
          Result.Status.ERROR -> {
//            Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
//              .show()
          }
          Result.Status.LOADING -> {

          }
        }
      }
    })
  }


  private fun showAnimations() {
    if (viewModel.appManager.loadingState.animation == "combo_added.json") {
      binding.llAnimation.llCongrats.visibility = View.VISIBLE
      binding.llAnimation.amount =
        "${appManager.persistenceManager.getCurrency()} ${viewModel.appManager.loadingState.amountSaved}"
    } else {
      binding.llAnimation.llCongrats.visibility = View.GONE
    }
    binding.llAnimation.clMain.visibility = View.VISIBLE
    binding.llAnimation.animationView.visibility = View.VISIBLE
    binding.llAnimation.animationView.setAnimation(viewModel.appManager.loadingState.animation)
    binding.llAnimation.animationView.repeatCount = 0
    binding.llAnimation.animationView.playAnimation()
    binding.llAnimation.animationView.addAnimatorListener(object : Animator.AnimatorListener {
      override fun onAnimationStart(animation: Animator?) {
        Log.e("Animation:", "start")
      }

      override fun onAnimationEnd(animation: Animator?) {
        Log.e("Animation:", "end")
        //Your code for remove the fragment
        viewModel.appManager.loadingState.setAnimationState(false)
      }

      override fun onAnimationCancel(animation: Animator?) {
        Log.e("Animation:", "cancel")
      }

      override fun onAnimationRepeat(animation: Animator?) {
        Log.e("Animation:", "repeat")
      }
    })
  }

  private fun hideAnimations() {
    binding.llAnimation.clMain.visibility = View.GONE
    binding.llAnimation.llCongrats.visibility = View.GONE
    binding.llAnimation.animationView.visibility = View.GONE
  }


  private fun initBottomNav() {
    binding.bottomNavigation.itemIconTintList = null
    binding.bottomNavigation.setupWithNavController(navController)
    binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
      onNavDestinationSelected(item, navController)
    }
    binding.bottomNavigation.setOnNavigationItemReselectedListener { item ->
      Log.e(TAG, "initBottomNav: item$item")

      if (item.itemId == R.id.nav_home) {
//        viewModel.appManager.loadingState.bottomReselected.value = true
        viewModel.appManager.loadingState.homeReselected.value = true
      }
      if (item.itemId == R.id.nav_categories) {
        viewModel.appManager.loadingState.categoryReselected.value = true
      }
      if (item.itemId == R.id.nav_prescription) {
        viewModel.appManager.loadingState.prescriptionReselected.value = true
      }
      if (item.itemId == R.id.nav_profile) {
        viewModel.appManager.loadingState.profileReselected.value = true
      }
      if (item.itemId == R.id.nav_cart) {
        viewModel.appManager.loadingState.cartReselected.value = true
      }

    }
  }

  override fun getLayoutRes(): Int {
    return R.layout.activity_dashboard_with_native_bottom
  }


  override fun permissionGranted(requestCode: Int) {
  }


  override fun getLoadingLayout(): ConstraintLayout {
    return binding.llLoading.clLoading
  }

  override fun onBackPressed() {
    stopLoading()
    super.onBackPressed()
  }

  override fun onRestart() {
    super.onRestart()
//    open(this)
  }

  override fun onClickExpand() {
    viewModel.isExpanded.observe(this, Observer {
      it?.let {
        if (it) {
          hideFloating()
        } else {
          expandFloating()
        }
      }
    })
    viewModel.isExpanded.value = viewModel.isExpanded.value != true
  }

  private fun expandFloating() {
    binding.llInstant.constraintLayout18.setTransition(R.id.end, R.id.start)
    binding.llInstant.constraintLayout18.setTransitionDuration(400)
    binding.llInstant.constraintLayout18.transitionToStart()
    binding.llInstant.constraintLayout18.transitionToEnd()
  }

  private fun hideFloating() {
    binding.llInstant.constraintLayout18.setTransition(R.id.start, R.id.end)
    binding.llInstant.constraintLayout18.setTransitionDuration(400)
    binding.llInstant.constraintLayout18.transitionToStart()
    binding.llInstant.constraintLayout18.transitionToEnd()
  }

  private fun observerNewRidBroadCast() {
    broadcastReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
          ConstantsUtil.IN_APP_POPUP -> {
            val notificationPayload: InAppNotificationMainModel? =
              intent.getParcelableExtra("payload")
            notificationPayload?.let { viewModel.addInAppNotificationQue(it) }
            if (notificationPayload != null) {
              navigateToInAppPopup()
            }
          }
          else -> {
          }
        }
      }
    }
    val filter = IntentFilter(ConstantsUtil.IN_APP_POPUP)
    registerReceiver(broadcastReceiver, filter)
  }


  private fun observerHomeNavBroadCast() {
    navBroadcastReceiver = object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
          ConstantsUtil.HOME_NAV -> {
            viewModel.notificationPayLoad.value =
              intent.getParcelableExtra("payload")
          }
          else -> {
          }
        }
      }
    }
    val filter = IntentFilter(ConstantsUtil.HOME_NAV)
    registerReceiver(navBroadcastReceiver, filter)
  }

  private fun navigateToInAppPopup() {
    if (!viewModel.notificationInAppList.isNullOrEmpty() && !isInAppPopUpOpened) {
      viewModel.notificationInAppList.first().let {
        viewModel.notificationModel.value = it
        dashboardRouter.openInAppPopUp()
      }

    }

  }


  private fun getUserMessages() {
    viewModel.getUserMessages().observe(this, {
      it?.let {
        when (it.status) {
          Result.Status.SUCCESS -> {
            it.data?.data?.let { it1 ->
              viewModel.filterUserMessagesTypes(it1)
              if (!it1.inAppMessages.isNullOrEmpty()) {
                navigateToInAppPopup()
              }
            }
          }
          Result.Status.ERROR -> {
            viewModelCart.isUpdatingCart.value = (true)
          }
          Result.Status.LOADING -> {
            viewModelCart.isUpdatingCart.value = (true)
          }
        }
      }
    })
  }

  override fun onClickClose(position: Int, stickyMessage: StickyMessageModel) {
    viewModel.changeStatusOfMessage(status = "closed", id = stickyMessage.id.toString())
    viewModel.removeStickyMessage(position)
  }

  override fun onClickView(stickyMessage: StickyMessageModel) {
    stickyMessage.conpaignId?.let { viewModel.appManager.persistenceManager.saveCompianID(it) }
    viewModel.changeStatusOfMessage(status = "viewed", id = stickyMessage.id.toString())
    val homeNavigation = HomeNavigation(this)
    homeNavigation.triggerNavigation(
      key = stickyMessage.data?.actionKey ?: "",
      value = stickyMessage.data?.actionValue ?: "",
      heading = stickyMessage.data?.actionLabel ?: ""
    )
  }

  override fun onClickOpenChat() {
    viewModel.messageOpened()
    dashboardRouter.openChat()
  }
}