package com.lifepharmacy.application.ui.orders.viewmodels

import android.app.Application
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.managers.MediaManagerKT
import com.lifepharmacy.application.model.orders.*
import com.lifepharmacy.application.model.payment.OrderPayment
import com.lifepharmacy.application.model.payment.TransactionModel
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.FileRepository
import com.lifepharmacy.application.repository.OrderRepository
import com.lifepharmacy.application.utils.universal.InputEditTextValidator
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.Throws

/**
 * Created by Zahid Ali
 */
class OrderDetailViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: OrderRepository,
  private val fileRepository: FileRepository,
  application: Application
) : BaseViewModel(application) {

  var subOrderItemMut = MutableLiveData<SubOrderDetail>()

  //  var orderModelMut = MutableLiveData<OrderDetailResponseModel>()
  var selectedShipmentMut = MutableLiveData<SubOrderItem>()
  var transactionModel = MutableLiveData<TransactionModel>()
  var isAnyProductToReturn = MutableLiveData<Boolean>()
  var returnArrayMut = MutableLiveData<ArrayList<ReturnOrderItem>>()
  var filesListLive = MutableLiveData<ArrayList<String>>()
  var isThereFiles = MutableLiveData<Boolean>()
  var stream = MutableLiveData<InputStream>()
  var orderId: String = ""
  var subOrderId: String = ""

  var cancelReason: String = ""

  var selectedSubOrderItem = MutableLiveData<SubOrderItem>()

  var productFeedback: String = ""
  var subOrderID: Int = 0
  var productRatingValue: Float = 0F
  var productID: String = ""

  var returnNote: InputEditTextValidator =
    InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      null
    )


  var invoiceFile: File? = null
  lateinit var invoicePath: String
  lateinit var invoiceURI: Uri
  fun getOrderDetails() =
    performNwOperation { repository.getOrderDetails(orderId) }

  fun getSubOrderDetail() =
    performNwOperation { repository.getSubOrderDetail(subOrderId) }

  fun returnItems() =
    performNwOperation { repository.getReturnOrder(makeReturnProductsModel()) }
  //  var returnArray = ArrayList<ProductModel>()


  fun uploadFile(file: File) =
    performNwOperation {
      fileRepository.uploadImage(
        MediaManagerKT.makeFilePart(
          MediaManagerKT.getCompressedPhoto(
            file,
            viewModelContext
          )
        )
      )
    }


  fun getReturnReasons() =
    performNwOperation { repository.getReturnReasons() }

  fun rateProduct() =
    performNwOperation { repository.rateProduct(productID ?: "0", makeProductRatingBody()) }


  fun downloadInvoice(url: String) =
    performNwOperation { repository.downlaodInvoice(url) }

  fun saveFile(body: ResponseBody?): File? {
    // Create the File where the photo should go
    invoiceFile = try {
      createImageFile()
    } catch (ex: IOException) {
      // Error occurred while creating the File
      null
    }
    // Continue only if the File was successfully created
//    invoiceFile?.also {
//      invoiceURI = FileProvider.getUriForFile(
//        viewModelContext,
//        "com.lifepharmacy.application.fileprovider",
//        it
//      )
//
//    }

    if (body == null)
      return null
    var input: InputStream? = null
    try {
      input = body.byteStream()

      invoiceFile?.writeBytes(input.readBytes())
      //val file = File(getCacheDir(), "cacheFileAppeal.srl")
//      val fos = FileOutputStream(pathWhereYouWantToSaveFile)
//      fos.use { output ->
//        val buffer = ByteArray(4 * 1024) // or other buffer size
//        var read: Int
//        while (input.read(buffer).also { read = it } != -1) {
//          output.write(buffer, 0, read)
//        }
//        output.flush()
//      }
      return invoiceFile
    } catch (e: Exception) {
      Log.e("saveFile", e.toString())
    }
    return null
  }

  fun File.copyInputStreamToFile(inputStream: InputStream) {
    this.outputStream().use { fileOut ->
      inputStream.copyTo(fileOut)
    }
  }

  fun setFileList(file: String) {
    val temp = filesListLive.value ?: ArrayList()
    temp.add(file)
    filesListLive.value = temp
  }

  fun removeFile(file: String) {
    val temp = filesListLive.value ?: ArrayList()
    temp.remove(file)
    filesListLive.value = temp
  }

  private fun makeReturnProductsModel(): ReturnOrderRequestBody {
    return ReturnOrderRequestBody(
      itemsToReturn = returnArrayMut.value,
      reason = Reason(
        images = filesListLive.value,
        reason = if (cancelReason.toLowerCase(Locale.ROOT) != "others") {
          cancelReason
        } else {
          returnNote.getValue()
        }
      )
    )
  }

  fun setReturnNoteError(reason: String) {
    if (reason.toLowerCase(Locale.ROOT) != "others") {
      returnNote.setError(false)
    } else {
      returnNote.validate()
    }
  }

  fun plusReturnItem(orderItem: SubOrderProductItem, qty: Int) {
    val temp = returnArrayMut.value ?: ArrayList()
    val tempOrderDetail = subOrderItemMut.value


    val subOrderFirstItem =
      tempOrderDetail?.items?.firstOrNull { product -> product.productDetails.id == orderItem.productDetails.id }

    if (subOrderFirstItem != null) {

      val returnOrderRequestModel =
        ReturnOrderItem(
          id = subOrderFirstItem.productDetails.id,
          qty = qty,
          subOrderId = tempOrderDetail.subOrders?.first()?.id
        )
      val returnItem =
        temp.firstOrNull { product -> product.id == returnOrderRequestModel.id }
      if (returnItem != null) {
        val indexOfProduct: Int? = temp.indexOf(returnItem)
        if (indexOfProduct != null) {
          temp.removeAt(indexOfProduct)
          temp.add(indexOfProduct, returnOrderRequestModel)
          returnArrayMut.value = temp
          return
        }
      } else {
        temp.add(returnOrderRequestModel)
        returnArrayMut.value = temp
        return
      }
    }


//    for (item in tempOrderDetail?.subOrders!!) {
//      val subOrder =
//        item.items?.firstOrNull { product -> product.productDetails.id == orderItem.productDetails.id }
//      if (subOrder != null) {
//        val returnOrderRequestModel =
//          ReturnOrderItem(id = subOrder.productDetails.id, qty = qty, subOrderId = item.id)
//        val returnItem =
//          temp.firstOrNull { product -> product.id == returnOrderRequestModel.id }
//        if (returnItem != null) {
//          val indexOfProduct: Int? = temp.indexOf(returnItem)
//          if (indexOfProduct != null) {
//            temp.removeAt(indexOfProduct)
//            temp.add(indexOfProduct, returnOrderRequestModel)
//            returnArrayMut.value = temp
//            return
//          }
//        } else {
//          temp.add(returnOrderRequestModel)
//          returnArrayMut.value = temp
//          return
//        }
//      }
//    }

  }

  fun clearReturnItems() {
    val temp = returnArrayMut.value ?: ArrayList()
    temp.clear()
    returnArrayMut.value = temp
  }

  fun minusReturnItem(orderItem: SubOrderProductItem, qty: Int) {
    val temp = returnArrayMut.value ?: ArrayList()
    val tempOrderDetail = subOrderItemMut.value

    val subOrderFirstItem =
      tempOrderDetail?.items?.firstOrNull { product -> product.productDetails.id == orderItem.productDetails.id }
    if (subOrderFirstItem != null) {
      val returnOrderRequestModel =
        ReturnOrderItem(
          id = subOrderFirstItem.productDetails.id,
          qty = qty,
          subOrderId = tempOrderDetail.id
        )

      val returnItem =
        temp.firstOrNull { product -> product.id == returnOrderRequestModel.id }
      if (returnItem != null) {
        val indexOfProduct: Int? = temp.indexOf(returnItem)
        if (indexOfProduct != null) {
          if (qty == 0) {
            temp.removeAt(indexOfProduct)
            returnArrayMut.value = temp
          } else {
            temp.removeAt(indexOfProduct)
            temp.add(indexOfProduct, returnOrderRequestModel)
            returnArrayMut.value = temp
          }
          return
        }
      }
    }

//    for (item in tempOrderDetail?.subOrders!!) {
//      val subOrder =
//        item.items?.firstOrNull { product -> product.productDetails.id == orderItem.productDetails.id }
//      if (subOrder != null) {
//        val returnOrderRequestModel =
//          ReturnOrderItem(id = subOrder.productDetails.id, qty = qty, subOrderId = item.id)
//        val returnItem =
//          temp.firstOrNull { product -> product.id == returnOrderRequestModel.id }
//        if (returnItem != null) {
//          val indexOfProduct: Int? = temp.indexOf(returnItem)
//          if (indexOfProduct != null) {
//            if (qty == 0) {
//              temp.removeAt(indexOfProduct)
//              returnArrayMut.value = temp
//            } else {
//              temp.removeAt(indexOfProduct)
//              temp.add(indexOfProduct, returnOrderRequestModel)
//              returnArrayMut.value = temp
//            }
//            return
//          }
//        }
//      }
//    }
  }

  fun checkIfItAlreadyAddedQty(orderItem: SubOrderProductItem): String? {
    val temp = returnArrayMut.value ?: ArrayList()
    val returnItem =
      temp.firstOrNull { product -> product.id == orderItem.productDetails.id }
    return returnItem?.qty?.toString()
  }

  private fun makeProductRatingBody(): RateProductRequestBody {
    return RateProductRequestBody(
      rating = productRatingValue,
      review = productFeedback,
      subOrderId = subOrderID,
      response = "new"
    )
  }

  @Throws(IOException::class)
  private fun createImageFile(): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("HHmmss").format(Date())
    val storageDir: File? = viewModelContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
    return File.createTempFile(
      "Invoice${timeStamp}_", /* prefix */
      ".pdf", /* suffix */
      storageDir /* directory */
    ).apply {
      // Save a file: path for use with ACTION_VIEW intents
      invoicePath = absolutePath
    }
  }

//  fun productReturnSelected(productModel: ProductModel) {
//    returnArray.add(productModel)
//    returnArrayMut.value = returnArray
//  }

}