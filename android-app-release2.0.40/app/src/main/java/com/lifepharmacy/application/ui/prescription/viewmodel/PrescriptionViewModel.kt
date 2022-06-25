package com.lifepharmacy.application.ui.prescription.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.managers.MediaManagerKT
import com.lifepharmacy.application.model.prescription.PrescriptionRequestBody
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.FileRepository
import com.lifepharmacy.application.repository.PrescriptionRepository
import com.lifepharmacy.application.enums.ImageUploadingType
import com.lifepharmacy.application.utils.universal.InputEditTextValidator
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import java.io.File

/**
 * Created by Zahid Ali
 */
class PrescriptionViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val repository: PrescriptionRepository,
  private val fileRepository: FileRepository,
  application: Application
) : BaseViewModel(application) {
  var isClaimInsurance = ObservableField<Boolean>()
  var isNotClaimInsurance = ObservableField<Boolean>()
  var isProceedEnable = MutableLiveData<Boolean>()
//  var isLoggedIn = MutableLiveData<Boolean>()

  var isThereFiles = ObservableField<Boolean>()
  var imageUploadingType = MutableLiveData<ImageUploadingType>()
  var prescriptionID = MutableLiveData<String>()
  var cardFrontUrl = ObservableField<String>()
  var cardBackUrl = ObservableField<String>()
  var insuranceURl = ObservableField<String>()
  var insuranceBackUrl = ObservableField<String>()
  var note: InputEditTextValidator =
    InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      null
    )
  var eRxNumber: InputEditTextValidator =
    InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      null
    )
  var insuranceNumber: InputEditTextValidator =
    InputEditTextValidator(
      InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
      true,
      null,
      null
    )


  fun uploadFile(file: File) =
    performNwOperation { fileRepository.uploadImage(MediaManagerKT.makeFilePart(MediaManagerKT.getCompressedPhoto(file,viewModelContext))) }

  fun uploadPrescription(id:String) =
    performNwOperation { repository.uploadPrescription(makePrescriptionRequestBody(id)) }

  var fileList = ArrayList<String>()
  var filesListLive = MutableLiveData<ArrayList<String>>()
  fun setFileList(file: String) {
    fileList.add(file)
    filesListLive.value = fileList
  }

  fun removeFile(file: String) {
    fileList.remove(file)
    filesListLive.value = fileList
  }


  private fun makePrescriptionRequestBody(id:String): PrescriptionRequestBody {
    return PrescriptionRequestBody(
//      eRxNumber = eRxNumber.getValue(),
//      insuranceMemberId = insuranceNumber.getValue(),
      prescription = fileList,
      notes = note.getValue(),
      emirateIdBack = cardBackUrl.get(),
      emirateIdFront = cardFrontUrl.get(),
      insuranceCardFront = insuranceURl.get(),
      insuranceCardBack = insuranceBackUrl.get(),
      addressId = id
    )
  }

  fun resetPrescription() {
    cardBackUrl.set(null)
    cardFrontUrl.set(null)
    insuranceURl.set(null)
    insuranceBackUrl.set(null)
    fileList.clear()
    filesListLive.value = fileList
    eRxNumber.setValue("")
    insuranceNumber.setValue("")
  }
}