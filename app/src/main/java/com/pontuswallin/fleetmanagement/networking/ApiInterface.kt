package com.pontuswallin.fleetmanagement.networking

import com.pontuswallin.fleetmanagement.model.RawDataAPIResponse
import com.pontuswallin.fleetmanagement.model.Vehicle
import com.pontuswallin.fleetmanagement.model.VehicleAPIResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("getLastData")
    fun getLastData(
        @Query("key") apiKey: String,
        @Query("json") json: Boolean
    ): Call<VehicleAPIResponse>

    @GET("getRawData")
    fun getLastDataByTimeRange(
        @Query("begTimestamp") begTimeStamp: String,
        @Query("endTimestamp") endTimestamp: String,
        @Query("objectId") objectId: Int,
        @Query("key") apiKey: String,
        @Query("json") json: Boolean
    ): Call<RawDataAPIResponse>
}
