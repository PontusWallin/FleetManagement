package com.pontuswallin.fleetmanagement.model

import com.google.gson.annotations.SerializedName

class RawDataAPIResponse (

    @SerializedName("status") val status : Int,

    @SerializedName("meta") val meta : Meta,

    @SerializedName("response") val response : List<VehicleLocation>,

    @SerializedName("errormessage") val errormessage : String
)