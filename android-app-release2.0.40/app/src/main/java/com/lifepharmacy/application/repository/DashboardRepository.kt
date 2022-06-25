package com.lifepharmacy.application.repository

import com.lifepharmacy.application.base.BaseRepository
import com.lifepharmacy.application.network.endpoints.DashboardApi
import com.lifepharmacy.application.utils.NetworkUtils
import javax.inject.Inject

class DashboardRepository
@Inject constructor(private val networkUtils: NetworkUtils, private val dashboardApi: DashboardApi) :
    BaseRepository() {
}