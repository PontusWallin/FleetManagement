package com.pontuswallin.fleetmanagement.model

import com.google.gson.annotations.SerializedName

class VehicleLocation (

    @SerializedName("timestamp") val timestamp : String,

    @SerializedName("ServerGenerated") val serverGenerated : String,

    @SerializedName("Din1") val din1 : String,

    @SerializedName("SplitSegment") val splitSegment : String,

    @SerializedName("EventType_dec") val eventType_dec : Int,

    @SerializedName("Distance") val distance : Double,

    @SerializedName("Power") val power : Double,

    @SerializedName("EngineStatus") val engineStatus : String,

    @SerializedName("Direction") val direction : Double,

    @SerializedName("Longitude") val longitude : Double,

    @SerializedName("Latitude") val latitude : Double,

    @SerializedName("GPSState") val gPSState : Int,

    @SerializedName("DriverId") val driverId : String,

    @SerializedName("Speed") val speed : String
    )