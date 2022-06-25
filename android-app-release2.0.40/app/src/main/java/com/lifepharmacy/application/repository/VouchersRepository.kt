package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.network.endpoints.VoucherApi
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class VouchersRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val voucherApi: VoucherApi) :
    BaseRepository() {
  suspend fun getVouchers(skip: String, take: String) =
    getResult({ voucherApi.getVouchersList(skip, take) }, networkUtils)
}