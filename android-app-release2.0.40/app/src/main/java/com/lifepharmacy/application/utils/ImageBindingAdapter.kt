package com.lifepharmacy.application.utils

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.maps.model.LatLng
import com.lifepharmacy.application.R
import com.lifepharmacy.application.enums.ShipmentType
import com.lifepharmacy.application.model.payment.TransactionMainModel
import com.lifepharmacy.application.model.product.Availability
import com.lifepharmacy.application.model.product.ProductDetails
import com.lifepharmacy.application.utils.universal.Extensions.stringToNullSafeDouble
import com.lifepharmacy.application.utils.universal.GoogleUtils
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


/**
 * Created by Zahid Ali
 */
class ImageBindingAdapter {
  companion object {
    @JvmStatic
    @BindingAdapter("loadImageFromFile")
    fun loadImageFromFile(
      view: ImageView,
      file: File?
    ) {
      if (file?.exists() == true) {
        val myBitmap = BitmapFactory.decodeFile(file.absolutePath)
        view.setImageBitmap(myBitmap)
      }
    }

    @JvmStatic
    @BindingAdapter("setTintColor")
    fun setTintColor(
      view: ImageView,
      color: String?
    ) {
      try {
        view.imageTintList = ColorStateList.valueOf(Color.parseColor(color))
      } catch (e: Exception) {

      }

    }

    @JvmStatic
    @BindingAdapter("setTextBackTint")
    fun setTextBackTint(
      view: TextView,
      color: String?
    ) {
      try {
        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
      } catch (e: Exception) {

      }

    }

    @JvmStatic
    @BindingAdapter(value = ["loadImageFromURl", "errorImage"], requireAll = false)
    fun loadImageFromURl(
      view: ImageView,
      imageUrl: String?,
      placeHolder: Drawable?
    ) {
//      CoroutineScope(Dispatchers.IO).launch {

//        val drawable =  Glide.with(view.context)
//          .asDrawable()
//          .load(imageUrl) // sample image
//          .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
//          .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
//          .submit()
//          .get()
//
//        showImage(drawable,view)
//      }
      val circularProgressDrawable = CircularProgressDrawable(view.context)
      circularProgressDrawable.strokeWidth = 5f
      circularProgressDrawable.centerRadius = 30f
      circularProgressDrawable.start()
      Glide.with(view.context)
        .load(imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
////        .apply(
////          RequestOptions()
////            .override(SIZE_ORIGINAL)
////            .format(DecodeFormat.PREFER_ARGB_8888)
////        )
//        .placeholder(circularProgressDrawable)
        .error(placeHolder)
        .placeholder(placeHolder)
        .into(view)
//      }
    }

    @JvmStatic
    @BindingAdapter(value = ["setTransactionLogo"], requireAll = false)
    fun setTransactionLogo(
      view: ImageView,
      transactionMainModel: TransactionMainModel?,
    ) {
      try {
        if (transactionMainModel != null) {

          if (transactionMainModel?.purpose == "wallet_recharge" && transactionMainModel?.method == "card") {
            view.setImageDrawable(
              ContextCompat.getDrawable(
                view.context,
                R.drawable.ic_arrow_up_dark_accent
              )
            )
          } else if (transactionMainModel?.purpose == "wallet_recharge" && transactionMainModel?.method == "refund") {
            view.setImageDrawable(
              ContextCompat.getDrawable(
                view.context,
                R.drawable.ic_icon___arrow___corner_right_up
              )
            )
          } else {
            view.setImageDrawable(
              ContextCompat.getDrawable(
                view.context,
                R.drawable.ic_walletarrow_down
              )
            )
          }
        } else {
          view.setImageDrawable(
            ContextCompat.getDrawable(
              view.context,
              R.drawable.ic_walletarrow_down
            )
          )
        }


      } catch (e: Exception) {

      }

    }

    @JvmStatic
    @BindingAdapter(value = ["loadImageFromURlGreyScale", "errorImage"], requireAll = false)
    fun loadImageFromURlGreyScale(
      view: ImageView,
      imageUrl: String?,
      placeHolder: Drawable?
    ) {
//      CoroutineScope(Dispatchers.IO).launch {

//        val drawable =  Glide.with(view.context)
//          .asDrawable()
//          .load(imageUrl) // sample image
//          .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
//          .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
//          .submit()
//          .get()
//
//        showImage(drawable,view)
//      }
      val circularProgressDrawable = CircularProgressDrawable(view.context)
      circularProgressDrawable.strokeWidth = 5f
      circularProgressDrawable.centerRadius = 30f
      circularProgressDrawable.start()
      Glide.with(view.context)
        .load(imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
////        .apply(
////          RequestOptions()
////            .override(SIZE_ORIGINAL)
////            .format(DecodeFormat.PREFER_ARGB_8888)
////        )
//        .placeholder(circularProgressDrawable)
        .error(placeHolder)
        .placeholder(placeHolder)
        .into(view)

      val colorMatrix = ColorMatrix()
      colorMatrix.setSaturation(0f)
      val filter = ColorMatrixColorFilter(colorMatrix)
      view.colorFilter = filter
//      }
    }

    @JvmStatic
    @BindingAdapter(value = ["loadingImageUsingPicasso", "errorImage"], requireAll = false)
    fun loadingImageUsingPicasso(
      view: ImageView,
      imageUrl: String?,
      placeHolder: Drawable?
    ) {
//      CoroutineScope(Dispatchers.IO).launch {

//        val drawable =  Glide.with(view.context)
//          .asDrawable()
//          .load(imageUrl) // sample image
//          .placeholder(android.R.drawable.progress_indeterminate_horizontal) // need placeholder to avoid issue like glide annotations
//          .error(android.R.drawable.stat_notify_error) // need error to avoid issue like glide annotations
//          .submit()
//          .get()
//
//        showImage(drawable,view)
//      }
//      val circularProgressDrawable = CircularProgressDrawable(view.context)
//      circularProgressDrawable.strokeWidth = 5f
//      circularProgressDrawable.centerRadius = 30f
//      circularProgressDrawable.start()
//      if (!imageUrl.isNullOrEmpty()) {
      if (placeHolder != null) {
        if (!imageUrl.isNullOrEmpty()) {
          CoroutineScope(Dispatchers.IO).launch {
            delay(50)
            CoroutineScope(Dispatchers.Main.immediate).launch {
              Picasso.get()
                .load(imageUrl)
                .error(placeHolder)
                .placeholder(placeHolder)
                .into(view)
            }

          }

        }
      }
//      }

//      }
    }

    @JvmStatic
    @BindingAdapter("loadImageWithOutCache")
    fun loadImageWithOutCache(
      view: ImageView,
      imageUrl: String?
    ) {
//      val circularProgressDrawable = CircularProgressDrawable(view.context)
//      circularProgressDrawable.strokeWidth = 5f
//      circularProgressDrawable.centerRadius = 30f
//      circularProgressDrawable.start()
      Glide.with(view.context)
        .load(imageUrl)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
//        .apply(
//          RequestOptions()
//            .override(SIZE_ORIGINAL)
//            .format(DecodeFormat.PREFER_ARGB_8888)
//        )
        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
        .into(view)
    }

    @JvmStatic
    @BindingAdapter("loadMinusORBasket")
    fun loadMinusORBasket(
      view: ImageView,
      qty: String?
    ) {
      qty?.let {
        if (qty.toInt() > 1) {
          view.setImageDrawable(
            ContextCompat.getDrawable(
              view.context,
              R.drawable.ic_minus_flat
            )
          )
        }
        if (qty.toInt() == 1) {
          view.setImageDrawable(
            ContextCompat.getDrawable(
              view.context,
              R.drawable.ic_basket_whit_border
            )
          )
        }
      }
    }


    @JvmStatic
    @BindingAdapter("availabilityForDetails", "enteredQty")
    fun setShipmentTypeTagForCartForDetails(
      view: ImageView,
      availabilityForDetails: Availability?,
      enteredQty: String?
    ) {
      val qty = enteredQty?.stringToNullSafeDouble()?.toInt() ?: 0
      when (availabilityForDetails?.getAvailability(qty)) {
        ShipmentType.INSTANT -> {
          view.visibility = View.VISIBLE
          view.setImageDrawable(
            ContextCompat.getDrawable(
              view.context,
              R.drawable.ic_instant_tag
            )
          )
        }
        ShipmentType.EXPRESS -> {
          view.visibility = View.VISIBLE
          view.setImageDrawable(
            ContextCompat.getDrawable(
              view.context,
              R.drawable.ic_express_tag
            )
          )
        }
        ShipmentType.STANDARD -> {
          view.visibility = View.VISIBLE
          view.setImageDrawable(
            ContextCompat.getDrawable(
              view.context,
              R.drawable.ic_express_tag
            )
          )
        }
        else -> {
          view.visibility = View.GONE
        }
      }

//      if (availability?.instant != null && availability.instant?.isAvailable == true) {
//        if (!enteredQty.isNullOrEmpty()) {
//          if (enteredQty.toDouble().toInt() > availability.instant?.qty!!) {
//            view.setImageDrawable(
//              ContextCompat.getDrawable(
//                view.context,
//                R.drawable.ic_express_tag
//              )
//            )
//          } else {
//            view.setImageDrawable(
//              ContextCompat.getDrawable(
//                view.context,
//                R.drawable.ic_instant_tag
//              )
//            )
//          }
//        } else {
//          view.setImageDrawable(
//            ContextCompat.getDrawable(
//              view.context,
//              R.drawable.ic_instant_tag
//            )
//          )
//        }
//      } else if (availability?.express?.isAvailable == true) {
//        view.setImageDrawable(
//          ContextCompat.getDrawable(
//            view.context,
//            R.drawable.ic_express_tag
//          )
//        )
//      }
    }

    @JvmStatic
    @BindingAdapter("availability", "enteredQty")
    fun setShipmentTypeTagForCart(
      view: ImageView,
      availability: Availability?,
      enteredQty: String?
    ) {
      val qty = enteredQty?.stringToNullSafeDouble()?.toInt() ?: 0
      when (availability?.getAvailability(qty)) {
        ShipmentType.INSTANT -> {
          view.visibility = View.VISIBLE
          view.setImageDrawable(
            ContextCompat.getDrawable(
              view.context,
              R.drawable.ic_instant_tag
            )
          )
        }
        ShipmentType.EXPRESS -> {
          view.visibility = View.GONE
          view.setImageDrawable(
            ContextCompat.getDrawable(
              view.context,
              R.drawable.ic_express_tag
            )
          )
        }
        ShipmentType.STANDARD -> {
          view.visibility = View.GONE
          view.setImageDrawable(
            ContextCompat.getDrawable(
              view.context,
              R.drawable.ic_express_tag
            )
          )
        }
        else -> {
          view.visibility = View.GONE
        }
      }

//      if (availability?.instant != null && availability.instant?.isAvailable == true) {
//        if (!enteredQty.isNullOrEmpty()) {
//          if (enteredQty.toDouble().toInt() > availability.instant?.qty!!) {
//            view.setImageDrawable(
//              ContextCompat.getDrawable(
//                view.context,
//                R.drawable.ic_express_tag
//              )
//            )
//          } else {
//            view.setImageDrawable(
//              ContextCompat.getDrawable(
//                view.context,
//                R.drawable.ic_instant_tag
//              )
//            )
//          }
//        } else {
//          view.setImageDrawable(
//            ContextCompat.getDrawable(
//              view.context,
//              R.drawable.ic_instant_tag
//            )
//          )
//        }
//      } else if (availability?.express?.isAvailable == true) {
//        view.setImageDrawable(
//          ContextCompat.getDrawable(
//            view.context,
//            R.drawable.ic_express_tag
//          )
//        )
//      }
    }

    @JvmStatic
    @BindingAdapter("availability")
    fun setShipmentTypeTag(
      view: ImageView,
      availability: Availability?,
    ) {
      if (availability?.instant != null && availability.instant?.isAvailable == true) {
        view.setImageDrawable(
          ContextCompat.getDrawable(
            view.context,
            R.drawable.ic_instant_tag
          )
        )

      } else {
        view.setImageDrawable(
          ContextCompat.getDrawable(
            view.context,
            R.drawable.ic_express_tag
          )
        )
      }
    }

    @JvmStatic
    @BindingAdapter(value = ["setAddCartVisibility", "isInCart"], requireAll = false)
    fun setAddCartVisibility(
      view: Button,
      availability: Availability?,
      isInCart: Boolean?,
    ) {
      if ((isInCart == true || availability == null)
        || (availability.instant?.isAvailable != true && availability.express?.isAvailable != true)
      ) {
        view.visibility = View.GONE
      } else {
        view.visibility = View.VISIBLE
      }
    }

    @JvmStatic
    @BindingAdapter("setClQTyVisibility")
    fun setClQTyVisibility(
      view: ConstraintLayout,
      availability: Availability?,
    ) {
      if (availability == null
        || (availability.instant?.isAvailable != true && availability.express?.isAvailable != true)
      ) {
        view.visibility = View.GONE
      } else {
        view.visibility = View.VISIBLE
      }
    }

    @JvmStatic
    @BindingAdapter("setNotifyBellVisibility")
    fun setNotifyBellVisibility(
      view: Button,
      availability: ProductDetails?,
    ) {
      when (availability?.stockAvailability()) {
        ShipmentType.OUT_OF_STOCK -> {

          view.visibility = View.VISIBLE
        }
        else -> {
          view.visibility = View.GONE
        }
      }
//      if (availability == null || (availability.instant?.isAvailable != true && availability.express?.isAvailable != true)
//      ) {
//        view.visibility = View.VISIBLE
//      } else {
//        view.visibility = View.GONE
//      }
    }

    @JvmStatic
    @BindingAdapter("setOrderOutOfStockVisibility")
    fun setOrderOutOfStockVisibility(
      view: Button,
      availability: ProductDetails?,
    ) {
      when (availability?.stockAvailability()) {
        ShipmentType.OUT_OF_STOCK -> {
          if (availability.unitPrice != null && availability.vatPercentageOutOfStock != null) {
            view.visibility = View.VISIBLE
          } else {
            view.visibility = View.GONE
          }
        }
        else -> {
          view.visibility = View.GONE
        }
      }
//      if (availability == null || (availability.instant?.isAvailable != true && availability.express?.isAvailable != true)
//      ) {
//        view.visibility = View.VISIBLE
//      } else {
//        view.visibility = View.GONE
//      }
    }

    @JvmStatic
    @BindingAdapter("setShippingTagVisibility")
    fun setShippingTagVisibility(
      view: ImageView,
      availability: ProductDetails?,
    ) {
      when (availability?.stockAvailability()) {
        ShipmentType.OUT_OF_STOCK -> {
          view.visibility = View.GONE
        }
        else -> {
          view.visibility = View.VISIBLE
        }
      }

//      if (availability == null || (availability.instant?.isAvailable != true && availability.express?.isAvailable != true)
//      ) {
//        view.visibility = View.GONE
//      } else {
//        view.visibility = View.VISIBLE
//      }
    }

    private fun showImage(bitmap: Drawable?, view: ImageView) {
      CoroutineScope(Dispatchers.Main).launch {
        view.setImageDrawable(
          bitmap
        )
      }
    }

    @JvmStatic
    @BindingAdapter(value = ["pickLatLong", "dropLatLong"], requireAll = false)
    fun loadMapUriImage(
      view: ImageView,
      pickAddress: LatLng = LatLng(0.0, 0.0), dropAddress: LatLng = LatLng(0.0, 0.0)
    ) {
      if (pickAddress.latitude != 0.0 && pickAddress.longitude != 0.0 && dropAddress.latitude != 0.0 && dropAddress.longitude != 0.0) {
        Glide.with(view.context)
          .load(
            GoogleUtils.staticMapWithPickAndDropOffUrl(
              pickAddress.latitude.toFloat(),
              pickAddress.longitude.toFloat(),
              dropAddress.latitude.toFloat(),
              dropAddress.longitude.toFloat()
            )
          )
          .centerCrop()
          .into(view)
      } else
        if ((pickAddress.latitude != 0.0 && pickAddress.longitude != 0.0) || (dropAddress.latitude != 0.0 && dropAddress.longitude != 0.0)) {
          if (pickAddress.latitude != 0.0 && pickAddress.longitude != 0.0) {
            Glide.with(view.context)
              .load(
                GoogleUtils.staticMapWithOneMarkerUrl(
                  pickAddress.latitude.toFloat(),
                  pickAddress.longitude.toFloat()
                )
              )
              .centerCrop()
              .into(view)
          }
          if (dropAddress.latitude != 0.0 && dropAddress.longitude != 0.0) {
            Glide.with(view.context)
              .load(
                GoogleUtils.staticMapWithOneMarkerUrl(
                  dropAddress.latitude.toFloat(),
                  dropAddress.longitude.toFloat()
                )
              )
              .centerCrop()
              .into(view)
          }
        }


//      pickAddress?.let { it ->
//        if (pickAddress.latitude != 0.0 && pickAddress.longitude != 0.0 && dropAddress?.latitude != 0.0 && dropAddress?.longitude != 0.0) {
//          Glide.with(view.context)
//            .load(dropAddress?.latitude?.toFloat()?.let { it1 ->
//              GoogleUtils.staticMapWithPickAndDropOffUrl(
//                it.latitude.toFloat(), it.longitude.toFloat(),
//                it1, dropAddress.longitude.toFloat()
//              )
//            })
//            .centerCrop()
//            .into(view)
//        }
//      }
    }

    @JvmStatic
    @BindingAdapter(value = ["prescriptionImageUploadingIcon", "errorImage"], requireAll = false)
    fun prescriptionImageUploadingIcon(
      view: ImageView,
      imageUrl: String?,
      placeHolder: Drawable?
    ) {
      if (imageUrl.isNullOrBlank()) {
        Glide.with(view.context)
          .load(R.drawable.ic_pluse_white_with_blue_back)
          .into(view)
      } else {
        val circularProgressDrawable = CircularProgressDrawable(view.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        Glide.with(view.context)
          .load(imageUrl)
////        .apply(
////          RequestOptions()
////            .override(SIZE_ORIGINAL)
////            .format(DecodeFormat.PREFER_ARGB_8888)
////        )
          .placeholder(R.drawable.ic_pluse_white_with_blue_back)
          .error(R.drawable.ic_pluse_white_with_blue_back)
//        .placeholder(placeHolder)
          .into(view)
//      }
      }

    }

  }


}