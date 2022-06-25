package com.lifepharmacy.application.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target

/**
 * <h>PicassoMarker</h>
 * <P>
 * class to create google map marker using the picasso
 * for the vehicle type image
</P> *
 * @since 9/10/15.
 */
class PicassoMarker(var mMarker: Marker) : Target {
    override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
        try {
            mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap))
        } catch (e: Exception) {
        }
    }

    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}

    /**
     * <h2>getmMarker</h2>
     *
     *
     * method to get the created marker
     *
     * @return
     */
    fun getmMarker(): Marker {
        return mMarker
    }

    override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
}