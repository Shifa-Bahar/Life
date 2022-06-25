package com.lifepharmacy.application.ui.address

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseActivity
import com.lifepharmacy.application.databinding.ActivityAddressSelectionBinding
import com.lifepharmacy.application.model.address.AddressModel
import dagger.hilt.android.AndroidEntryPoint


private const val KEY_RESULT = "RESULT"
private const val KEY_NAME = "NAME"

@AndroidEntryPoint
class AddressSelectionActivity : BaseActivity<ActivityAddressSelectionBinding>() {
  private val viewModelAddress: AddressViewModel by viewModels()

  companion object {
    fun open(activity: Activity) {
      val intent = Intent(activity, AddressSelectionActivity::class.java)
      activity.startActivity(intent)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModelAddress.addressConfirmed.value = false
    viewModelAddress.isSelecting.set(intent?.getBooleanExtra(KEY_NAME, false))
    observer()
  }

  fun observer() {
    viewModelAddress.addressConfirmed.observe(this, {
      it?.let {
        if (it) {
          setResult(999, Intent().putExtra(KEY_RESULT, viewModelAddress.deliveredAddressMut.value))
          finish()
        }
      }
    })
  }

  override fun getLayoutRes(): Int {
    return R.layout.activity_address_selection
  }

  override fun getLoadingLayout(): ConstraintLayout {
    return binding.llLoading.clLoading
  }

  override fun permissionGranted(requestCode: Int) {

  }

  class Contract : ActivityResultContract<Boolean, TransactionResult>() {
    override fun createIntent(context: Context, input: Boolean?): Intent =
      Intent(context, AddressSelectionActivity::class.java)
        .putExtra(KEY_NAME, input)

    override fun parseResult(resultCode: Int, intent: Intent?) = TransactionResult(
      success = resultCode == RESULT_OK,
      address = intent?.getParcelableExtra(KEY_RESULT)
    )
  }
}

class TransactionResult(val success: Boolean, val address: AddressModel?)