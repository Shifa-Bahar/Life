package com.lifepharmacy.application.ui.dashboard.adapter

import com.lifepharmacy.application.model.blog.BlogModel
import com.lifepharmacy.application.model.notifications.StickyMessageModel

/**
 * Created by Zahid Ali
 */
interface ClickStickyMessage {
  fun onClickClose(position: Int, stickyMessage: StickyMessageModel)
  fun onClickView(stickyMessage: StickyMessageModel)

}