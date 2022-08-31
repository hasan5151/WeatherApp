package com.huzi.shared.retrofit.interceptor

import com.huzi.shared.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class CityAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val headersBuilder = request.headers.newBuilder()
        headersBuilder.add("Authorization", "Bearer ${BuildConfig.CITY_TOKEN}")

        val headers = headersBuilder.build()
        request = request.newBuilder().headers(headers).build()
        return chain.proceed(request)
    }
}
