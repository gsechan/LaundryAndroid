package com.gabesechan.laundrydemo.laundromatinfo

import com.gabesechan.laundrydemo.network.NetworkResponse
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Headers
import java.math.BigDecimal
import java.util.UUID

@Serializable
data class AvailableTimesResponse(
    val pickup: List<AvailableDateTime>,
    val delivery: List<AvailableDateTime>,
    val minTimeBetweenPickupAndDelivery: Long //ms minimum between pickup and delivery
)

@Serializable
data class AvailableDateTime(
    val date: Long, //UTC millis of midnight on that day
    val times: List<TimeRange>
)

@Serializable
data class TimeRange(val startTime: Long, val endTime:Long) //Start and end of a range, in ms from midnight

@Serializable
data class PricesResponse(val washFold: Int, val shirts: Int, val pants: Int, val dress: Int, val suit: Int)

@Serializable
data class WashFoldResponse(val price: String, val avgWeight: String)

@Serializable
data class DryCleanItemsResponse(
    val items: List<JSONDryCleanItem>
)

@Serializable
data class JSONDryCleanItem(
    val id: String,
    val name: String,
    val price: String
)


interface LaundromatInfoServer {


    @Headers("Content-Type: application/json")
    @GET("availableTimes")
    suspend fun availableTimes(): NetworkResponse<AvailableTimesResponse>

    @Headers("Content-Type: application/json")
    @GET("washFold")
    suspend fun washFold(): NetworkResponse<WashFoldResponse>

    @Headers("Content-Type: application/json")
    @GET("dryCleanItem")
    suspend fun dryCleanItems(): NetworkResponse<DryCleanItemsResponse>
}