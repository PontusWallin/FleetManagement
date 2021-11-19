package com.pontuswallin.fleetmanagement.utilities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class GoogleMapsUtils {

    companion object {

        val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION

        var LOCATION_PERMISSION_REQUEST_CODE = 1234

        private val ERROR_DIALOG_REQUEST = 9001
        fun isGoogleMapsServiceAvailable(activity: Activity): Boolean {

            val available = GoogleApiAvailability
                .getInstance().isGooglePlayServicesAvailable(activity)

            if(available == ConnectionResult.SUCCESS) {
                Log.d(ContentValues.TAG, "Google PLay Service is working")
                return true
            } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
                Log.d(ContentValues.TAG, "An error occurred, but we can fix it")
                createGoogleAPIErrorDialog(activity, available)
                return false
            } else {
                Toast.makeText(activity, "You can't make map requests.", Toast.LENGTH_SHORT).show()
            }
            return false
        }

        private fun createGoogleAPIErrorDialog(activity: Activity, available: Int) {
            val dialog = GoogleApiAvailability.getInstance().getErrorDialog(
                activity,
                available,
                ERROR_DIALOG_REQUEST
            )
            dialog.show()
        }
    }
}