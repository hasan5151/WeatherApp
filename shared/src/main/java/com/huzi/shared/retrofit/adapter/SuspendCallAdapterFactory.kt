package com.huzi.shared.retrofit.adapter

import com.huzi.shared.result.ApiResponse
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class SuspendCallAdapterFactory : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): SuspendCallAdapter<*>? {
        // suspend functions wrap the response type in `Call`
        if (getRawType(returnType) != Call::class.java) {
            return null
        }

        // check first that the return type is `ParameterizedType`
        check(returnType is ParameterizedType) {
            "return type must be parameterized as Call<ApiResponse<Foo>> or Call<ApiResponse<out Foo>>"
        }

        // get the response type inside the `Call` type
        val responseType = getParameterUpperBound(0, returnType)
        // if the response type is not ApiResponse then we can't handle this type, so we return null
        if (getRawType(responseType) != ApiResponse::class.java) {
            return null
        }

        // the response type is ApiResponse and should be parameterized
        check(responseType is ParameterizedType) {
            "Response must be parameterized as ApiResponse<Foo> or ApiResponse<out Foo>"
        }

        val bodyType = getParameterUpperBound(0, responseType)

        return SuspendCallAdapter<Any>(bodyType)
    }
}