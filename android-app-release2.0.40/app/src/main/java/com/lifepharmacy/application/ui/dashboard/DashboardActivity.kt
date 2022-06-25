package com.lifepharmacy.application.ui.dashboard

import android.animation.Animator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseActivity
import com.lifepharmacy.application.databinding.ActivityDashboardBinding
import com.lifepharmacy.application.model.BottomBarModel
import com.lifepharmacy.application.network.Result
import com.lifepharmacy.application.ui.address.AddressViewModel
import com.lifepharmacy.application.ui.cart.viewmodel.CartViewModel
import com.lifepharmacy.application.ui.dashboard.viewmodel.DashboardViewModel
import com.lifepharmacy.application.utils.AnalyticsUtil
import com.lifepharmacy.application.utils.universal.ConnectionLiveData
import com.lifepharmacy.application.utils.universal.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*


@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>(), ClickBottomNav {
  private val viewModel: DashboardViewModel by viewModels()
  private val viewModelAddress: AddressViewModel by viewModels()


  private lateinit var navController: NavController
  var debouncePeriod: Long = 1500
  var bottomNavDebounce: Long = 30
  var transitionDelay: Long = 0

  var activityResumed= false

  private val coroutineScope = lifecycle.coroutineScope

  private var navigationJob: Job? = null
  private var animationJob: Job? = null

  //  private var lastSelectBottomSheetIndex = 0
  lateinit var list: ArrayList<BottomBarModel>

  companion object {
    fun open(activity: Activity) {
      val intent = Intent(activity, DashboardWithNativeBottomActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      activity.startActivity(intent)
    }
  }

  override fun onResume() {
    super.onResume()
//    AnalyticsUtil.setSingleLog(applicationContext,"AppResumedFromBackground")
//    observerCurrentFragment()
    activityResumed = true
    observer()
    viewModel.offersManagers.loadCartFromPref(this)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val connectionLiveData =
      ConnectionLiveData(this)
    connectionLiveData.observe(this, { isConnected ->
      isConnected?.let {
        if (it) {
//          Toast.makeText(this, "Network Connected", Toast.LENGTH_SHORT).show()
        } else {
//          Toast.makeText(this, "Network Disconnected", Toast.LENGTH_SHORT).show()
        }
        // do job
      }
    })
    if (viewModel.appManager.persistenceManager.isThereCart()) {
      updateCartFromServer()
    }
    supportFragmentManager
    navController = Navigation.findNavController(this, R.id.fragment)
    list = ArrayList()
    list.add(BottomBarModel("Home", "home.json", "home_rev.json"))
    list.add(BottomBarModel("Category", "category.json", "category_rev.json"))
    list.add(BottomBarModel("Prescription", "prescription.json", "prescription_rev.json"))
    list.add(BottomBarModel("Profile", "profile.json", "profile_rev.json"))
    list.add(BottomBarModel("Cart", "cart.json", "cart_rev.json"))

    initBottomNav()


  }

  fun observer() {
    viewModel.offersManagers.cartQtyCountMut.observe(this, Observer {
      it?.let {
        if (it > 0) {
          binding.bottomNavigation.llCart.tvCartNumber.text = it.toString()
          binding.bottomNavigation.llCart.tvCartNumber.visibility = View.VISIBLE
        } else {
          binding.bottomNavigation.llCart.tvCartNumber.visibility = View.GONE
        }
//        if (!viewModel.offersManagers.isUpdatingFromServer) {
//          searchJob?.cancel()
//          searchJob = coroutineScope.launch {
//            delay(debouncePeriod)
////            updateServerCart()
//
//          }
//        }
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
    viewModel.selectedFragmentIndexMut.observe(this, Observer {
      it?.let {
        viewModel.selectedFragmentIndex = it
        if (viewModel.lastSelectBottomSheetIndex != it && !activityResumed){
          selectBottomNavWithNavigation(it)
        }
      }
    })
  }

  private fun updateCartFromServer() {
    viewModel.getCartDetails().observe(this, Observer {
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

//  private fun updateServerCart() {
//    if (viewModel.appManager.persistenceManager.isThereCart()) {
//      updateCartObserver()
//    } else {
//      createCartObserver()
//    }
//  }
//
//  private fun createCartObserver() {
//    viewModel.createCart().observe(this, Observer {
//      it?.let {
//        when (it.status) {
//          Result.Status.SUCCESS -> {
//            it.data?.data?.items?.let { it1 ->
//              viewModel.offersManagers.updateCartFromServer(
//                this,
//                it1
//              )
//            }
//            appManager.persistenceManager.saveCartID(it.data?.data?.id.toString())
//          }
//          Result.Status.ERROR -> {
////            Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
////              .show()
//          }
//          Result.Status.LOADING -> {
//
//          }
//        }
//      }
//    })
//  }
//
//  private fun updateCartObserver() {
//    viewModel.updateCart().observe(this, Observer {
//      it?.let {
//        when (it.status) {
//          Result.Status.SUCCESS -> {
//            it.data?.data?.items?.let { it1 ->
//              viewModel.offersManagers.updateCartFromServer(
//                this,
//                it1
//              )
//            }
//            appManager.persistenceManager.saveCartID(it.data?.data?.id.toString())
//          }
//          Result.Status.ERROR -> {
////            Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
////              .show()
//          }
//          Result.Status.LOADING -> {
//
//          }
//        }
//      }
//    })
//  }

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

    binding.bottomNavigation.click = this
    binding.bottomNavigation.llHome.tvTitle.text = list[0].name
    binding.bottomNavigation.llCategory.tvTitle.text = list[1].name
    binding.bottomNavigation.llPrescription.tvTitle.text = list[2].name
    binding.bottomNavigation.llProfile.tvTitle.text = list[3].name
    binding.bottomNavigation.llCart.tvTitle.text = list[4].name

    binding.bottomNavigation.llHome.animationView.setAnimation(list[0].selectedAnimation)
    binding.bottomNavigation.llCategory.animationView.setAnimation(list[1].selectedAnimation)
    binding.bottomNavigation.llPrescription.animationView.setAnimation(list[2].selectedAnimation)
    binding.bottomNavigation.llProfile.animationView.setAnimation(list[3].selectedAnimation)
    binding.bottomNavigation.llCart.animationView.setAnimation(list[4].selectedAnimation)

    binding.bottomNavigation.llHome.animationView.playAnimation()
    binding.bottomNavigation.llHome.tvTitle.setTextColor(
      ContextCompat.getColor(
        this,
        R.color.accent_blue_darker
      )
    )
  }

  override fun getLayoutRes(): Int {
    return R.layout.activity_dashboard
  }


  override fun permissionGranted(requestCode: Int) {
  }

  override fun onClickHome() {
    selectedAnimation(0)
  }

  override fun onClickCategory() {
    selectedAnimation(1)
  }

  override fun onClickPrescription() {

    selectedAnimation(2)


  }

  override fun onClickProfile() {

    selectedAnimation(3)

  }

  override fun onClickCart() {

    selectedAnimation(4)


  }

  private fun selectBottomNavWithNavigation(index: Int) {
    animationJob?.cancel()
    animationJob = coroutineScope.launch {
      delay(bottomNavDebounce)
      when (index) {
        0 -> {
          if (viewModel.lastSelectBottomSheetIndex != 0) {
            reversOldAnimation()
            viewModel.lastSelectBottomSheetIndex = 0
            binding.bottomNavigation.llHome.animationView.setAnimation(list[0].selectedAnimation)
            binding.bottomNavigation.llHome.animationView.playAnimation()
            binding.bottomNavigation.llHome.tvTitle.setTextColor(
              ContextCompat.getColor(
                this@DashboardActivity,
                R.color.accent_blue_darker
              )
            )
//            CoroutineScope(Dispatchers.Main.immediate).launch {
//              delay(200)
//
//
//            }
          }


        }
        1 -> {
          if (viewModel.lastSelectBottomSheetIndex != 1) {
            reversOldAnimation()
            viewModel.lastSelectBottomSheetIndex = 1
            binding.bottomNavigation.llCategory.animationView.setAnimation(list[1].selectedAnimation)
            binding.bottomNavigation.llCategory.animationView.playAnimation()

            binding.bottomNavigation.llCategory.tvTitle.setTextColor(
              ContextCompat.getColor(
                this@DashboardActivity,
                R.color.accent_blue_darker
              )
            )
//            CoroutineScope(Dispatchers.Main.immediate).launch {
//
//            }
          }



        }
        2 -> {
          if (viewModel.lastSelectBottomSheetIndex != 2) {
            reversOldAnimation()
            viewModel.lastSelectBottomSheetIndex = 2
            binding.bottomNavigation.llPrescription.animationView.setAnimation(list[2].selectedAnimation)
            binding.bottomNavigation.llPrescription.animationView.playAnimation()
            binding.bottomNavigation.llPrescription.tvTitle.setTextColor(
              ContextCompat.getColor(
                this@DashboardActivity,
                R.color.accent_blue_darker
              )
            )
//            CoroutineScope(Dispatchers.Main.immediate).launch {
//
//            }
          }




        }
        3 -> {
          if (viewModel.lastSelectBottomSheetIndex != 3) {
            reversOldAnimation()
            viewModel.lastSelectBottomSheetIndex = 3
            binding.bottomNavigation.llProfile.animationView.setAnimation(list[3].selectedAnimation)
            binding.bottomNavigation.llProfile.animationView.playAnimation()
            binding.bottomNavigation.llProfile.tvTitle.setTextColor(
              ContextCompat.getColor(
                this@DashboardActivity,
                R.color.accent_blue_darker
              )
            )
//            CoroutineScope(Dispatchers.Main.immediate).launch {
//
//            }
          }
//          CoroutineScope(Dispatchers.Main.immediate).launch {
//            delay(200)
//            navController.navigate(R.id.toProfile)
//          }




        }
        4 -> {
          if (viewModel.lastSelectBottomSheetIndex != 4) {
            reversOldAnimation()
            viewModel.lastSelectBottomSheetIndex = 4
            binding.bottomNavigation.llCart.animationView.setAnimation(list[4].selectedAnimation)
            binding.bottomNavigation.llCart.animationView.playAnimation()
            binding.bottomNavigation.llCart.tvTitle.setTextColor(
              ContextCompat.getColor(
                this@DashboardActivity,
                R.color.accent_blue_darker
              )
            )
//            CoroutineScope(Dispatchers.Main.immediate).launch {
//
//            }
          }



        }
      }

    }

  }

  private fun selectedAnimation(index: Int) {
    navigationJob?.cancel()
    navigationJob = coroutineScope.launch {
      delay(bottomNavDebounce)
      when (index) {
        0 -> {
          navigateToHome()
        }
        1 -> {
          navigateToCategories()
        }
        2 -> {
          navigateToPrescriptions()
        }
        3 -> {

          navigateToProfile()
        }
        4 -> {
          navigateToCart()
        }
      }

    }

  }

  private fun reversOldAnimation() {
    when (viewModel.lastSelectBottomSheetIndex) {
      0 -> {
        binding.bottomNavigation.llHome.animationView.setAnimation(list[viewModel.lastSelectBottomSheetIndex].unselectedAnimation)
        binding.bottomNavigation.llHome.animationView.playAnimation()
        binding.bottomNavigation.llHome.tvTitle.setTextColor(
          ContextCompat.getColor(
            this,
            R.color.color_1d1d
          )
        )

      }
      1 -> {
        binding.bottomNavigation.llCategory.animationView.setAnimation(list[viewModel.lastSelectBottomSheetIndex].unselectedAnimation)
        binding.bottomNavigation.llCategory.animationView.playAnimation()
        binding.bottomNavigation.llCategory.tvTitle.setTextColor(
          ContextCompat.getColor(
            this,
            R.color.color_1d1d
          )
        )

      }
      2 -> {
        binding.bottomNavigation.llPrescription.animationView.setAnimation(list[viewModel.lastSelectBottomSheetIndex].unselectedAnimation)
        binding.bottomNavigation.llPrescription.animationView.playAnimation()
        binding.bottomNavigation.llPrescription.tvTitle.setTextColor(
          ContextCompat.getColor(
            this,
            R.color.color_1d1d
          )
        )

      }
      3 -> {
        binding.bottomNavigation.llProfile.animationView.setAnimation(list[viewModel.lastSelectBottomSheetIndex].unselectedAnimation)
        binding.bottomNavigation.llProfile.animationView.playAnimation()
        binding.bottomNavigation.llProfile.tvTitle.setTextColor(
          ContextCompat.getColor(
            this,
            R.color.color_1d1d
          )
        )

      }
      4 -> {
        binding.bottomNavigation.llCart.animationView.setAnimation(list[viewModel.lastSelectBottomSheetIndex].unselectedAnimation)
        binding.bottomNavigation.llCart.animationView.playAnimation()
        binding.bottomNavigation.llCart.tvTitle.setTextColor(
          ContextCompat.getColor(
            this,
            R.color.color_1d1d
          )
        )

      }
    }
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

  private fun navigateToHome() {
    if (viewModel.lastSelectBottomSheetIndex != 0) {
      Utils.vibrate(this)
      reversOldAnimation()
      viewModel.lastSelectBottomSheetIndex = 0
      binding.bottomNavigation.llHome.animationView.setAnimation(list[0].selectedAnimation)
      binding.bottomNavigation.llHome.animationView.playAnimation()
      binding.bottomNavigation.llHome.tvTitle.setTextColor(
        ContextCompat.getColor(
          this@DashboardActivity,
          R.color.accent_blue_darker
        )
      )
      CoroutineScope(Dispatchers.Main.immediate).launch {
        delay(transitionDelay)

        navController.navigate(R.id.toHome)
      }
    }
  }

  private fun navigateToCategories() {
    if (viewModel.lastSelectBottomSheetIndex != 1) {
      Utils.vibrate(this)
      reversOldAnimation()
      viewModel.lastSelectBottomSheetIndex = 1
      binding.bottomNavigation.llCategory.animationView.setAnimation(list[1].selectedAnimation)
      binding.bottomNavigation.llCategory.animationView.playAnimation()
      CoroutineScope(Dispatchers.Main.immediate).launch {
        delay(transitionDelay)

        navController.navigate(R.id.toCategories)
      }
      binding.bottomNavigation.llCategory.tvTitle.setTextColor(
        ContextCompat.getColor(
          this@DashboardActivity,
          R.color.accent_blue_darker
        )
      )
    }
  }

  private fun navigateToPrescriptions() {
    if (viewModel.lastSelectBottomSheetIndex != 2) {
      reversOldAnimation()
      viewModel.lastSelectBottomSheetIndex = 2
      Utils.vibrate(this)
      CoroutineScope(Dispatchers.Main.immediate).launch {
        delay(transitionDelay)
        navController.navigate(R.id.toPrescription)

      }

      binding.bottomNavigation.llPrescription.animationView.setAnimation(list[2].selectedAnimation)
      binding.bottomNavigation.llPrescription.animationView.playAnimation()
      binding.bottomNavigation.llPrescription.tvTitle.setTextColor(
        ContextCompat.getColor(
          this@DashboardActivity,
          R.color.accent_blue_darker
        )
      )
    }

  }
  private fun navigateToProfile(){
    if (viewModel.lastSelectBottomSheetIndex != 3) {
      reversOldAnimation()
      viewModel.lastSelectBottomSheetIndex = 3
      Utils.vibrate(this)
      CoroutineScope(Dispatchers.Main.immediate).launch {
        delay(transitionDelay)
        navController.navigate(R.id.toProfile)

      }

      binding.bottomNavigation.llProfile.animationView.setAnimation(list[3].selectedAnimation)
      binding.bottomNavigation.llProfile.animationView.playAnimation()
      binding.bottomNavigation.llProfile.tvTitle.setTextColor(
        ContextCompat.getColor(
          this@DashboardActivity,
          R.color.accent_blue_darker
        )
      )
    }
  }
  private fun navigateToCart(){
    if (viewModel.lastSelectBottomSheetIndex != 4) {
      reversOldAnimation()
      viewModel.lastSelectBottomSheetIndex = 4
      Utils.vibrate(this)
      CoroutineScope(Dispatchers.Main.immediate).launch {
        delay(transitionDelay)
        navController.navigate(R.id.toCart)
      }

      binding.bottomNavigation.llCart.animationView.setAnimation(list[4].selectedAnimation)
      binding.bottomNavigation.llCart.animationView.playAnimation()
      binding.bottomNavigation.llCart.tvTitle.setTextColor(
        ContextCompat.getColor(
          this@DashboardActivity,
          R.color.accent_blue_darker
        )
      )
    }

  }

//  private fun observerCurrentFragment() {
//    navController.addOnDestinationChangedListener { _, destination, _ ->
//      when (destination.id) {
//        R.id.home -> {
//          if (viewModel.lastSelectBottomSheetIndex != 0) {
//            selectBottomNavWithNavigation(0)
//          }
//        }
//        R.id.categoriesFragment -> {
//          if (viewModel.lastSelectBottomSheetIndex != 1) {
//            selectBottomNavWithNavigation(1)
//          }
//        }
//        R.id.prescriptionFragment -> {
//          if (viewModel.lastSelectBottomSheetIndex != 2) {
//            selectBottomNavWithNavigation(2)
//          }
//        }
//        R.id.profileFragment -> {
//          if (viewModel.lastSelectBottomSheetIndex != 3) {
//            selectBottomNavWithNavigation(3)
//          }
//        }
//        R.id.cartFragment -> {
//          if (viewModel.lastSelectBottomSheetIndex != 4) {
//            selectBottomNavWithNavigation(4)
//          }
//        }
//      }
//    }
//  }

  override fun onStop() {
//    AnalyticsUtil.setSingleLog(applicationContext,"AppSentToBackground")/
    super.onStop()
  }
}