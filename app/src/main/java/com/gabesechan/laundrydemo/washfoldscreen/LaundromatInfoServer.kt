package com.gabesechan.laundrydemo.washfoldscreen

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
data class PricesResponse(val washFold: Int, val shirts: Int, val pants: Int, val dress: Int, val suit: Int)


interface LaundromatInfoServer {


    @Headers("Content-Type: application/json")
    @GET("availableTimes")
    suspend fun availableTimes(): AvailableTimesResponse

    @Headers("Content-Type: application/json")
    @GET("prices")
    suspend fun prices(): PricesResponse
}