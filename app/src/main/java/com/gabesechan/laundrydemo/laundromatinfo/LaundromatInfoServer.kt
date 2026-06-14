package com.gabesechan.laundrydemo.laundromatinfo

import com.gabesechan.laundrydemo.models.Item
import com.gabesechan.laundrydemo.network.NetworkResponse
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Headers

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
data class ItemsResponse(
    val items: List<Item>
)


interface LaundromatInfoServer {


    @Headers("Content-Type: application/json")
    @GET("availableTimes")
    suspend fun availableTimes(): NetworkResponse<AvailableTimesResponse>

    @Headers("Content-Type: application/json")
    @GET("items")
    suspend fun items(): NetworkResponse<ItemsResponse>
}