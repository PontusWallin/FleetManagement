package com.pontuswallin.fleetmanagement.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Vehicle (

    @SerializedName("objectId") val objectId : Int?,

    @SerializedName("orgId") val orgId : Int?,

    @SerializedName("timestamp") val timestamp : String?,

    @SerializedName("latitude") val latitude : Double?,

    @SerializedName("longitude") val longitude : Double?,

    @SerializedName("speed") val speed : Int?,

    @SerializedName("enginestate") val enginestate : Int?,

    @SerializedName("gpsstate") val gpsstate : Boolean?,

    @SerializedName("direction") val direction : Int?,

    @SerializedName("fuel") val fuel : String?,

    @SerializedName("power") val power : Double?,

    @SerializedName("CANDistance") val cANDistance : String?,

    @SerializedName("available") val available : String?,

    @SerializedName("driverId") val driverId : Int?,

    @SerializedName("driverName") val driverName : String?,

    @SerializedName("driverKey") val driverKey : String?,

    @SerializedName("driverPhone") val driverPhone : String?,

    @SerializedName("driverStatuses") val driverStatuses : List<String>?,

    @SerializedName("driverIsOnDuty") val driverIsOnDuty : Boolean?,

    @SerializedName("dutyTags") val dutyTags : List<String>?,

    @SerializedName("pairedObjectId") val pairedObjectId : String?,

    @SerializedName("pairedObjectName") val pairedObjectName : String?,

    @SerializedName("lastEngineOnTime") val lastEngineOnTime : String?,

    @SerializedName("inPrivateZone") val inPrivateZone : Boolean?,

    @SerializedName("offWorkSchedule") val offWorkSchedule : Boolean?,

    @SerializedName("tripPurposeDinSet") val tripPurposeDinSet : String?,

    @SerializedName("tcoData") val tcoData : String?,

    @SerializedName("tcoCardIsPresent") val tcoCardIsPresent : Boolean?,

    @SerializedName("address") val address : String?,

    @SerializedName("addressArea") val addressArea : Boolean?,

    @SerializedName("addressAreaId") val addressAreaId : String?,

    @SerializedName("displayColor") val displayColor : String?,

    @SerializedName("employeeId") val employeeId : String?,

    @SerializedName("currentOdometer") val currentOdometer : String?,

    @SerializedName("currentWorkhours") val currentWorkhours : String?,

    @SerializedName("enforcePrivacyFilter") val enforcePrivacyFilter : String?,

    @SerializedName("EVStateOfCharge") val eVStateOfCharge : String?,

    @SerializedName("EVDistanceRemaining") val eVDistanceRemaining : String?,

    @SerializedName("customValues") val customValues : List<String>?,

    @SerializedName("EventType") val eventType : Int?,

    @SerializedName("objectName") val objectName : String?,

    @SerializedName("externalId") val externalId : String?,

    @SerializedName("plate") val plate : String?
) : Parcelable