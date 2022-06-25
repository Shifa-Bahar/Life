package com.lifepharmacy.application.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OkHTTPClientNormal

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OkHTTPClientSearch

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitNormal

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitGoogleRoute

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitSearch

