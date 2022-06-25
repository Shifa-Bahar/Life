package com.lifepharmacy.application.di

import android.content.Context
import com.google.gson.GsonBuilder
import com.lifepharmacy.application.Constants
import com.lifepharmacy.application.managers.*
import com.lifepharmacy.application.managers.analytics.AnalyticsManagers
import com.lifepharmacy.application.network.AuthInterceptor
import com.lifepharmacy.application.network.SearchAuthInterceptor
import com.lifepharmacy.application.network.TokenAuthenticator
import com.lifepharmacy.application.network.endpoints.*
import com.lifepharmacy.application.repository.SplashRepository
import com.lifepharmacy.application.repository.WishListRepository
import com.lifepharmacy.application.ui.utils.AppScrollState
import com.lifepharmacy.application.ui.utils.LoadingState
import com.lifepharmacy.application.utils.universal.Extensions.ignoreAllSSLErrors
import com.lifepharmacy.application.utils.universal.GoogleUtils
import com.lifepharmacy.application.utils.universal.GpsStatusListener
import com.lifepharmacy.application.utils.NetworkUtils
import com.lifepharmacy.application.utils.URLs
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.*


@InstallIn(ApplicationComponent::class)
@Module
object AppModule {
  @Singleton
  @Provides
  fun providePersistenceManager(@ApplicationContext context: Context): PersistenceManager {
    return PersistenceManager(context)
  }

  @Provides
  fun provideSplashApi(@RetrofitNormal retrofit: Retrofit): SplashApi =
    retrofit.create(SplashApi::class.java)

  @Provides
  fun provideSplashRepository(
    networkUtils: NetworkUtils,
    api: SplashApi
  ): SplashRepository {
    return SplashRepository(networkUtils, api)
  }

  @Provides
  fun provideVoucherApi(@RetrofitNormal retrofit: Retrofit): VoucherApi =
    retrofit.create(VoucherApi::class.java)

  @Provides
  fun provideRewardsApi(@RetrofitNormal retrofit: Retrofit): RewardsApi =
    retrofit.create(RewardsApi::class.java)

  @Provides
  fun provideS3Api(): S3Api =
    getRetrofitForS3().create(S3Api::class.java)

  @Singleton
  @Provides
  fun provideGPSListener(@ApplicationContext context: Context): GpsStatusListener {
    return GpsStatusListener(context)
  }


  @Singleton
  @Provides
  fun provideLoadingState(): LoadingState {
    return LoadingState()
  }

  @Singleton
  @Provides
  fun provideNetworkUtils(
    @ApplicationContext context: Context,
    persistenceManager: PersistenceManager
  ): NetworkUtils {
    return NetworkUtils(context, persistenceManager)
  }

  @Singleton
  @Provides
  fun provideGoogleUtils(@ApplicationContext context: Context): GoogleUtils {
    return GoogleUtils(context)
  }

  @Singleton
  @Provides
  fun providePusherManager(
    @ApplicationContext context: Context,
    persistenceManager: PersistenceManager,
  ): PusherManager {
    return PusherManager(context, persistenceManager = persistenceManager)
  }

  @Singleton
  @Provides
  fun providePusherBeamManager(
    @ApplicationContext context: Context,
    persistenceManager: PersistenceManager
  ): PusherBeamManager {
    return PusherBeamManager(context, persistenceManager)
  }

  //  @Singleton
//  @Provides
//  fun provideCartUtils(persistenceManager: PersistenceManager,offersManagers: OffersManagers): CartManager {
//    return CartManager(persistenceManager,offersManagers)
//  }
  @Singleton
  @Provides
  fun provideStorageManager(
    persistenceManager: PersistenceManager,
    networkUtils: NetworkUtils,
    splashApi: SplashApi,
    voucherApi: VoucherApi,
    RewardsApi: RewardsApi,
    s3Api: S3Api
  ): StorageManagers {
    return StorageManagers(persistenceManager, networkUtils, splashApi, voucherApi, s3Api = s3Api)
  }

  @Singleton
  @Provides
  fun provideAnalyticsManagers(
    @ApplicationContext context: Context,
    persistenceManager: PersistenceManager,
    networkUtils: NetworkUtils,
    storageManagers: StorageManagers,
    pusherBeamManager: PusherBeamManager
  ): AnalyticsManagers {
    return AnalyticsManagers(
      context,
      persistenceManager,
      networkUtils,
      storageManagers,
      pusherBeamManager
    )
  }

  @Singleton
  @Provides
  fun provideOfferManager(
    appManager: PersistenceManager,
    loadingState: LoadingState,
    analyticsManagers: AnalyticsManagers

  ): OffersManagers {
    return OffersManagers(appManager, loadingState, analyticsManagers)
  }

  @Singleton
  @Provides
  fun provideAddressManager(persistenceManager: PersistenceManager): AddressManager {
    return AddressManager(persistenceManager)
  }

  @Singleton
  @Provides
  fun provideFilterManager(persistenceManager: PersistenceManager): FiltersManager {
    return FiltersManager(persistenceManager)
  }

  @Singleton
  @Provides
  fun provideAppManager(
    @ApplicationContext context: Context,
    persistenceManager: PersistenceManager,
    googleUtils: GoogleUtils,
    filtersManager: FiltersManager,
    offersManagers: OffersManagers,
    wishListManager: WishListManager,
    loadingState: LoadingState,
    scrollState: AppScrollState,
    storageManagers: StorageManagers,
    networkUtils: NetworkUtils,
    pusherManager: PusherManager,
    analyticsManagers: AnalyticsManagers,
    eventlistmainManager: EventlistmainManager

  ): AppManager {
    return AppManager(
      context,
      persistenceManager,
      googleUtils,
      filtersManager,
      offersManagers,
      wishListManager,
      loadingState,
      scrollState,
      storageManagers,
      networkUtils,
      pusherManager,
      analyticsManagers,
      eventlistmainManager
    )
  }

  @Singleton
  @Provides
  fun providesTokenAuthenticator(@ApplicationContext appContext: Context): TokenAuthenticator {
    return TokenAuthenticator(appContext)
  }

  @Singleton
  @Provides
  fun providesInterceptor(
    @ApplicationContext appContext: Context,
    persistenceManager: PersistenceManager,
    networkUtils: NetworkUtils
  ): Interceptor {
    return AuthInterceptor(appContext, persistenceManager, networkUtils)
  }


  @Singleton
  @Provides
  fun providesSearchAuthInterceptor(
    storageManagers: StorageManagers,
  ): SearchAuthInterceptor {
    return SearchAuthInterceptor(storageManagers = storageManagers)
  }

  //  tokenAuthenticator: TokenAuthenticator,
  @Singleton
  @Provides
  @OkHTTPClientNormal
  fun providesHttpClient(
    @ApplicationContext appContext: Context,
    networkUtils: NetworkUtils,
    persistenceManager: PersistenceManager
  ): OkHttpClient {

    val cacheSize = (58 * 1024 * 1024).toLong()
    val myCache = Cache(appContext.cacheDir, cacheSize)

    val loggingInterceptor = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }
    val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(
      TrustManagerFactory.getDefaultAlgorithm()
    )
    trustManagerFactory.init(null as KeyStore?)
    val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
    check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
      ("Unexpected default trust managers:"
          + trustManagers.contentToString())
    }
    val trustManager: X509TrustManager = trustManagers[0] as X509TrustManager

    val sslContext: SSLContext = SSLContext.getInstance("SSL")
    sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
    val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
    if (com.lifepharmacy.application.BuildConfig.BUILD_TYPE == "dev") {
      return OkHttpClient().newBuilder()
        .apply {
          // ...
//        if (BuildConfig.DEBUG) //if it is a debug build ignore ssl errors
          ignoreAllSSLErrors()
          //...
//          if (BuildConfig.FLAVOR == "dev"){
//            addInterceptor(ChuckInterceptor(appContext))
//          }
        }
        .addInterceptor(ChuckInterceptor(appContext))
        .addInterceptor(loggingInterceptor)
        .addInterceptor(providesInterceptor(appContext, persistenceManager, networkUtils))
        .connectTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()
    } else {
      return OkHttpClient().newBuilder()
        .apply {
          // ...
//        if (BuildConfig.DEBUG) //if it is a debug build ignore ssl errors
          ignoreAllSSLErrors()
          //...
        }
//        .addInterceptor(ChuckInterceptor(appContext))
        .addInterceptor(loggingInterceptor)
        .addInterceptor(providesInterceptor(appContext, persistenceManager, networkUtils))
        .connectTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()
    }


  }


  @Singleton
  @Provides
  @OkHTTPClientSearch
  fun providesHttpClientForSearch(
    @ApplicationContext appContext: Context,
    searchAuthInterceptor: SearchAuthInterceptor
  ): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }
    if (com.lifepharmacy.application.BuildConfig.BUILD_TYPE == "dev") {
      return OkHttpClient().newBuilder()
        .apply {
          ignoreAllSSLErrors()
        }
        .addInterceptor(ChuckInterceptor(appContext))
        .addInterceptor(loggingInterceptor)
        .addInterceptor(searchAuthInterceptor)
        .connectTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()
    } else {
      return OkHttpClient().newBuilder()
        .apply {
          ignoreAllSSLErrors()
        }
        .addInterceptor(loggingInterceptor)
        .addInterceptor(searchAuthInterceptor)
        .connectTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()
    }

  }

  @Singleton
  @Provides
  @RetrofitNormal
  fun providesRetrofit(@OkHTTPClientNormal okHttpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(Constants.BASE_URL)
      .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
      .build()

  @Singleton
  @Provides
  @RetrofitGoogleRoute
  fun providesMapRetrofit(@OkHTTPClientNormal okHttpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(URLs.GOOGLE_MAP_BASE_URL)
      .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
      .build()


  @Singleton
  @Provides
  @RetrofitSearch
  fun providesRetrofitForSearch(
    @OkHTTPClientSearch okHttpClient: OkHttpClient,
    storageManagers: StorageManagers,
  ): Retrofit =
    Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(storageManagers.config.customBaseURL ?: Constants.BASE_URL)
      .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
      .build()

  @Singleton
  @Provides
  fun provideScrollState(): AppScrollState {
    return AppScrollState()
  }

  @Singleton
  @Provides
  fun provideWishListApi(@RetrofitNormal retrofit: Retrofit): WishListApi =
    retrofit.create(WishListApi::class.java)

  @Singleton
  @Provides
  fun getPaymentApi(): PaymentApi =
    getRetrofitForLifePay().create(PaymentApi::class.java)

  @Singleton
  @Provides
  fun provideWishListRepository(
    networkUtils: NetworkUtils,
    api: WishListApi
  ): WishListRepository {
    return WishListRepository(networkUtils, api)
  }

  @Singleton
  @Provides
  fun provideWishListManager(
    persistenceManager: PersistenceManager,
    wishListResp: WishListRepository,
    analyticsManagers: AnalyticsManagers
  ): WishListManager {
    return WishListManager(persistenceManager, wishListResp, analyticsManagers)
  }


  private fun getRetrofitForS3(): Retrofit {
    val gson = GsonBuilder().setLenient().create()
    return Retrofit.Builder()
      .client(getOkHttpClientForS3())
      .baseUrl(Constants.LIFE_S3_BASE_URL)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
  }

  private fun getRetrofitForLifePay(): Retrofit {
    val gson = GsonBuilder().create()
    return Retrofit.Builder()
      .client(getOkHttpClientAuth())
      .baseUrl(Constants.LIFE_PAY_BASE_URL)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
  }


  private fun getOkHttpClientAuth(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }
    val trustManagerFactory: TrustManagerFactory = TrustManagerFactory.getInstance(
      TrustManagerFactory.getDefaultAlgorithm()
    )
    trustManagerFactory.init(null as KeyStore?)
    val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
    check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
      ("Unexpected default trust managers:"
          + trustManagers.contentToString())
    }
    val trustManager: X509TrustManager = trustManagers[0] as X509TrustManager

    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
    val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
    return OkHttpClient().newBuilder()
      .addInterceptor(loggingInterceptor)
      .connectTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .sslSocketFactory(sslSocketFactory, trustManager)
      .build()
  }

  private fun getOkHttpClientForS3(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
      level = HttpLoggingInterceptor.Level.BODY
    }
    return OkHttpClient().newBuilder()
      .addInterceptor(loggingInterceptor)
      .connectTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .build()
  }
}





