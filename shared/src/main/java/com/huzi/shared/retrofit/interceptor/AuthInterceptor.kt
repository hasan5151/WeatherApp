package com.huzi.shared.retrofit.interceptor

import com.huzi.shared.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val newParams = request.url.newBuilder()
            .addQueryParameter("appid",BuildConfig.APPID)
            .addQueryParameter("units","metric") //For temperature in Celsius
            .build()
        request = request.newBuilder().url(newParams).build()
        return chain.proceed(request)
    }
}
