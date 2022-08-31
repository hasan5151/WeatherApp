package com.huzi.shared.retrofit.adapter

import com.huzi.shared.result.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class SuspendCallAdapter<R>(private val responseType: Type) : CallAdapter<R, Call<ApiResponse<R>>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<R>): Call<ApiResponse<R>> {
        return SuspendCall(call)
    }
}