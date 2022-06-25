package com.lifepharmacy.application.ui.documents.adapters

import com.lifepharmacy.application.model.address.AddressModel
import com.lifepharmacy.application.model.docs.DocumentModel

/**
 * Created by Zahid Ali
 */
interface ClickItemDoc {
  fun onClickDeleteDoc(doc:DocumentModel)
  fun onClickDoc(doc:DocumentModel,post:Int)
}