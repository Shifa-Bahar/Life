package com.lifepharmacy.application.utils

import com.lifepharmacy.application.model.product.ProductRating
import com.lifepharmacy.application.utils.universal.Extensions.intToNullSafeDouble
import kotlin.math.roundToInt

/**
 * Created by Zahid Ali
 */
object RatingUtil {
  fun calculateRatingFor1(rating: ProductRating): Int {
    return if (rating.ratingDetails.x1 > 0) {
      val perForOne: Double =
        (rating.ratingDetails.x1.intToNullSafeDouble() / sumOFRating(rating)) * 100
      perForOne.roundToInt()
    } else {
      0
    }
  }

  fun calculateRatingFor2(rating: ProductRating): Int {
    return if (rating.ratingDetails.x2 > 0) {
      val perForOne: Double =
        (rating.ratingDetails.x2.intToNullSafeDouble() / sumOFRating(rating)) * 100
      perForOne.roundToInt()
    } else {
      0
    }
  }

  fun calculateRatingFor3(rating: ProductRating): Int {
    return if (rating.ratingDetails.x3 > 0) {
      val perForOne: Double =
        (rating.ratingDetails.x3.intToNullSafeDouble() / sumOFRating(rating)) * 100
      perForOne.roundToInt()
    } else {
      0
    }
  }

  fun calculateRatingFor4(rating: ProductRating): Int {
    return if (rating.ratingDetails.x4 > 0) {
      val perForOne: Double =
        (rating.ratingDetails.x4.intToNullSafeDouble() / sumOFRating(rating)) * 100
      perForOne.roundToInt()
    } else {
      0
    }

  }

  fun calculateRatingFor5(rating: ProductRating): Int {
    return if (rating.ratingDetails.x5 > 0) {
      val perForOne: Double =
        (rating.ratingDetails.x5.intToNullSafeDouble() / sumOFRating(rating)) * 100
      perForOne.roundToInt()
    } else {
      0
    }

  }

  private fun sumOFRating(rating: ProductRating): Int {
    return rating.ratingDetails.x1 + rating.ratingDetails.x2 + rating.ratingDetails.x3 + rating.ratingDetails.x4 + rating.ratingDetails.x5
  }
}