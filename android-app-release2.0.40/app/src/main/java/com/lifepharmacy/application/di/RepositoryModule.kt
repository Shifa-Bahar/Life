package com.lifepharmacy.application.di

import com.google.gson.GsonBuilder
import com.lifepharmacy.application.Constants
import com.lifepharmacy.application.network.endpoints.*
import com.lifepharmacy.application.repository.*
import com.lifepharmacy.application.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

@Module
@InstallIn(ActivityRetainedComponent::class)
object RepositoryModule {
  @Provides
  fun provideAuthApi(@RetrofitNormal retrofit: Retrofit): AccountApi =
    retrofit.create(AccountApi::class.java)


  @Provides
  fun provideCustomSearchApi(@RetrofitSearch retrofit: Retrofit): CustomSearchApi =
    retrofit.create(CustomSearchApi::class.java)

  @Provides
  fun provideAuthRepository(
    networkUtils: NetworkUtils,
    authApi: AccountApi
  ): AuthRepository {
    return AuthRepository(networkUtils, authApi)
  }

  @Provides
  fun provideHomeApi(@RetrofitNormal retrofit: Retrofit): HomeApi =
    retrofit.create(HomeApi::class.java)

  @Provides
  fun provideRatingApi(@RetrofitNormal retrofit: Retrofit): RatingApi =
    retrofit.create(RatingApi::class.java)


  @Provides
  fun provideRatingRepository(
    networkUtils: NetworkUtils,
    api: RatingApi
  ): RatingRepository {
    return RatingRepository(networkUtils, api)
  }

  @Provides
  fun getBlogApi(): BlogApi =
    getRetrofitForBlog().create(BlogApi::class.java)

  @Provides
  fun provideHomeRepository(
    networkUtils: NetworkUtils,
    homeApi: HomeApi,
    blogApi: BlogApi
  ): HomeRepository {
    return HomeRepository(networkUtils, homeApi, blogApi)
  }

  @Provides
  fun provideProfileApi(@RetrofitNormal retrofit: Retrofit): ProfileApi =
    retrofit.create(ProfileApi::class.java)

  @Provides
  fun provideProfileRepository(
    networkUtils: NetworkUtils,
    profileApi: ProfileApi,
    googleMapApi: GoogleMapApi
  ): ProfileRepository {
    return ProfileRepository(networkUtils, profileApi, googleMapApi)
  }

  @Provides
  fun provideProductApi(@RetrofitNormal retrofit: Retrofit): ProductApi =
    retrofit.create(ProductApi::class.java)


  @Provides
  fun provideGoogleMapApi(@RetrofitGoogleRoute retrofit: Retrofit): GoogleMapApi =
    retrofit.create(GoogleMapApi::class.java)

  @Provides
  fun provideProductRepository(
    networkUtils: NetworkUtils,
    productApi: ProductApi
  ): ProductRepository {
    return ProductRepository(networkUtils, productApi)
  }

  @Provides
  fun provideProductListApi(@RetrofitNormal retrofit: Retrofit): ProductsApi =
    retrofit.create(ProductsApi::class.java)

  @Provides
  fun provideProductListRepository(
    networkUtils: NetworkUtils,
    products: ProductsApi
  ): ProductListRepository {
    return ProductListRepository(networkUtils, products)
  }

  @Provides
  fun PcrlistingApi(@RetrofitNormal retrofit: Retrofit): PcrlistingApi =
    retrofit.create(PcrlistingApi::class.java)

  @Provides
  fun providePcrListingRepository(
    networkUtils: NetworkUtils,
    pcrproductList: PcrlistingApi
  ): PcrListingRepository {
    return PcrListingRepository(networkUtils, pcrproductList)
  }

  @Provides
  fun provideCartApi(@RetrofitNormal retrofit: Retrofit): CartApi =
    retrofit.create(CartApi::class.java)

  @Provides
  fun provideCartRepository(
    networkUtils: NetworkUtils,
    cartApi: CartApi
  ): CartRepository {
    return CartRepository(networkUtils, cartApi)
  }


  @Provides
  fun provideVoucherRepository(
    networkUtils: NetworkUtils,
    voucherApi: VoucherApi
  ): VouchersRepository {
    return VouchersRepository(networkUtils, voucherApi)
  }


  @Provides
  fun provideRewardsRepository(
    networkUtils: NetworkUtils,
    RewardsApi: RewardsApi
  ): RewardsRepository {
    return RewardsRepository(networkUtils, RewardsApi)
  }


//  @Provides
//  fun provideEventRepository(
//    networkUtils: NetworkUtils,
//    EventsApi: EventsApi
//  ): EventRepository {
////    return EventRepository(networkUtils, EventsApi)
//  }

  @Provides
  fun provideOrderApi(@RetrofitNormal retrofit: Retrofit): OrdersApi =
    retrofit.create(OrdersApi::class.java)

  @Provides
  fun provideOrderRepository(
    networkUtils: NetworkUtils,
    api: OrdersApi
  ): OrderRepository {
    return OrderRepository(networkUtils, api)
  }

  @Provides
  fun provideWalletApi(@RetrofitNormal retrofit: Retrofit): WalletApi =
    retrofit.create(WalletApi::class.java)

  @Provides
  fun provideWalletRepository(
    networkUtils: NetworkUtils,
    walletApi: WalletApi
  ): WalletRepository {
    return WalletRepository(networkUtils, walletApi)
  }

  @Provides
  fun provideSearchApi(@RetrofitNormal retrofit: Retrofit): SearchApi =
    retrofit.create(SearchApi::class.java)

  @Provides
  fun provideSearchRepository(
    networkUtils: NetworkUtils,
    api: SearchApi,
    customSearchApi: CustomSearchApi
  ): SearchRepository {
    return SearchRepository(networkUtils, api, customSearchApi)
  }

  @Provides
  fun provideCategoryApi(@RetrofitNormal retrofit: Retrofit): CategoryApi =
    retrofit.create(CategoryApi::class.java)

  @Provides
  fun provideCategoryRepository(
    networkUtils: NetworkUtils,
    api: CategoryApi
  ): CategoryRepository {
    return CategoryRepository(networkUtils, api)
  }

  @Provides
  fun provideFileApi(@RetrofitNormal retrofit: Retrofit): FileApi =
    retrofit.create(FileApi::class.java)

  @Provides
  fun provideFileRepository(
    networkUtils: NetworkUtils,
    api: FileApi
  ): FileRepository {
    return FileRepository(networkUtils, api)
  }

  @Provides
  fun providePrescriptionApi(@RetrofitNormal retrofit: Retrofit): PrescriptionApi =
    retrofit.create(PrescriptionApi::class.java)

  @Provides
  fun providePrescriptionRepository(
    networkUtils: NetworkUtils,
    api: PrescriptionApi
  ): PrescriptionRepository {
    return PrescriptionRepository(networkUtils, api)
  }

  @Provides
  fun providePageApi(@RetrofitNormal retrofit: Retrofit): PageApi =
    retrofit.create(PageApi::class.java)

  @Provides
  fun providePageRepository(
    networkUtils: NetworkUtils,
    api: PageApi
  ): PageRepository {
    return PageRepository(networkUtils, api)
  }

  private fun getRetrofitWithCache(): Retrofit {
    val gson = GsonBuilder().create()
    return Retrofit.Builder()
      .client(getOkHttpClientWithCache())
      .baseUrl(Constants.BASE_URL)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
  }

  private fun getOkHttpClientWithCache(): OkHttpClient {
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

  private fun getRetrofitForBlog(): Retrofit {
    val gson = GsonBuilder().create()
    return Retrofit.Builder()
      .client(getOkHttpClientAuth())
      .baseUrl(Constants.BLOG_BASE_URL)
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
}














