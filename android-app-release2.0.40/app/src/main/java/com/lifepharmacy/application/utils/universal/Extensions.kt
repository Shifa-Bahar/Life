package com.lifepharmacy.application.utils.universal

import com.lifepharmacy.application.utils.universal.Extensions.doubleDigitDouble
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDoubleByDefault1
import okhttp3.OkHttpClient
import java.math.BigDecimal
import java.math.RoundingMode
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * Created by Zahid Ali
 */
object Extensions {
  //    fun Double.currencyFormat(): String {
//        return String.format("%.2f", this)
//    }
  fun Double.currencyFormat(): String {
    return try {
      String.format("%.2f", this)
    } catch (e: Exception) {
      "0.00"
    }
//  return String.format("%.2f", this)
//    val decimal = BigDecimal(this).setScale(2, RoundingMode.UP)
//    return decimal.toString()
  }

  fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
    val naiveTrustManager = object : X509TrustManager {
      override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
      override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
      override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
    }

    val insecureSocketFactory = SSLContext.getInstance("TLSv1.2").apply {
      val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
      init(null, trustAllCerts, SecureRandom())
    }.socketFactory

    sslSocketFactory(insecureSocketFactory, naiveTrustManager)
    hostnameVerifier(HostnameVerifier { _, _ -> true })
    return this
  }

  fun Double.doubleDigitDouble(): Double {
    return try {
      String.format("%.2f", this).stringToNullSafeDouble()
    } catch (e: Exception) {
      0.00
    }

  }

  fun String.stringToNullSafeDouble(): Double {
    return try {
      this.toDouble()
    } catch (e: Exception) {
      0.00
    }

  }

  fun Int.intToNullSafeDouble(): Double {
    return try {
      this.toDouble()
    } catch (e: Exception) {
      0.00
    }

  }

  fun String.stringToNullSafeInt(): Int {
    return try {
      if (this == "0") {
        1
      } else {
        this.toInt()
      }

    } catch (e: Exception) {
      1
    }

  }

  fun Int.intToNullSafeDoubleByDefault1(): Double {
    return try {
      if (this == 0) {
        1.0
      } else {
        this.toDouble()
      }

    } catch (e: Exception) {
      1.00
    }

  }
}