package com.lifepharmacy.application.ui.documents.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.lifepharmacy.application.R
import com.lifepharmacy.application.base.BaseViewModel
import com.lifepharmacy.application.enums.DocTypeFilter
import com.lifepharmacy.application.managers.AppManager
import com.lifepharmacy.application.managers.MediaManagerKT
import com.lifepharmacy.application.model.docs.CreateDocRequestBody
import com.lifepharmacy.application.model.docs.DeleteDocRequestBody
import com.lifepharmacy.application.model.docs.DocumentModel
import com.lifepharmacy.application.network.performNwOperation
import com.lifepharmacy.application.repository.ProfileRepository
import kotlin.collections.ArrayList
import com.lifepharmacy.application.repository.FileRepository
import com.lifepharmacy.application.utils.universal.InputEditTextValidator
import com.lifepharmacy.application.utils.universal.Utils
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import java.io.File

/**
 * Created by Zahid Ali
 */
class DocumentsViewModel
@ViewModelInject
constructor(
  val appManager: AppManager,
  private val profileRepository: ProfileRepository,
  private val fileRepository: FileRepository,
  application: Application
) : BaseViewModel(application) {
  var errorText = viewModelContext.getString(R.string.fieldError)

  var docTitle = InputEditTextValidator(
    InputEditTextValidator.InputEditTextValidationsEnum.FIELD,
    true,
    null,
    errorText
  )
  var docType = MutableLiveData<String>()
  var fileUrl = MutableLiveData<String>()
  var selectedDoc = MutableLiveData<DocumentModel>()
  var docTypeFilter = MutableLiveData<DocTypeFilter>()
  var showBottomView = MutableLiveData<Boolean>()
  var isProfile = false
  var listOfDocs = ArrayList<DocumentModel>()
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

  fun getDocs() =
    performNwOperation { profileRepository.getDocs() }

  fun createDoc() =
    performNwOperation { profileRepository.createDocs(makeCreateDocBody()) }

  fun deleteDoc() =
    performNwOperation { profileRepository.deleteDoc(makeDeleteDocBody()) }

  private fun makeCreateDocBody(): CreateDocRequestBody {
    return CreateDocRequestBody(
      fileName = docTitle.getValue(),
      fileType = Utils.getDocTypeForSend(docType.value ?: "emirates_id_front"),
      fileUrl = fileUrl.value
    )
  }

  private fun makeDeleteDocBody(): DeleteDocRequestBody {
    return DeleteDocRequestBody(
      fileId = selectedDoc.value?.id
    )
  }
}