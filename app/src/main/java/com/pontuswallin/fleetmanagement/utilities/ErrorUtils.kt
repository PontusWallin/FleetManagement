package com.pontuswallin.fleetmanagement.utilities

import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import com.pontuswallin.fleetmanagement.model.APIError

class ErrorUtils {
    companion object {

        fun displayErrorDialog(context: Context, error: APIError) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Error " + error.statuscode + " - " + error.message)
            builder.setMessage("Please try again later or contact support.")
            builder.setPositiveButton("OK") { _: DialogInterface, _: Int -> }

            return builder.create().show()
        }
    }
}