package com.gabesechan.laundrydemo.network

import kotlinx.serialization.Serializable

@Serializable
class NetworkResponse<T>(
    val success: Boolean,
    val errorType: String?,
    val error: String?,
    val data: T?,
) {
    fun process(): T {
        if(success) {
            if(data != null) {
                return data
            }
            throw IllegalArgumentException("Null data on success from server.")
        }
        if(errorType == "API_SPECIFIC_ERROR") {
            throw APISpecificException(error!!)
        }
        if(errorType == "BAD_AUTH") {
            throw BadAuthException()
        }

        //Handle different generic error cases.  For example, I think we want to handle logout here.
        throw IllegalArgumentException("Unknown error type:  ${errorType?:"null"}.  Error is ${error?:"null"}")
    }
}

