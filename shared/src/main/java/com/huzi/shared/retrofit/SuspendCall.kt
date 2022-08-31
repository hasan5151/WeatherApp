package com.huzi.shared.retrofit

import com.huzi.shared.result.ApiResponse
import retrofit2.Response

suspend fun <T> suspendApiCall(apiService: suspend () -> Response<T>): ApiResponse<T> {
    return try {
        ApiResponse.create(apiService.invoke())
    } catch (ex: Exception) {
        ApiResponse.create(ex)
    }
}