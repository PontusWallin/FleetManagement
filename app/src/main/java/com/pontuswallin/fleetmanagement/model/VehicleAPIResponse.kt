package com.pontuswallin.fleetmanagement.model

import com.google.gson.annotations.SerializedName

class VehicleAPIResponse (

    @SerializedName("status") val status : Int,

    @SerializedName("meta") val meta : Meta,

    @SerializedName("response") val response : List<Vehicle>,

    @SerializedName("errormessage") val errormessage : String
)