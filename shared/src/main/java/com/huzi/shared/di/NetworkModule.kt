package com.huzi.shared.di

import com.huzi.shared.BuildConfig
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.huzi.shared.moshi.BonusMoshiAdapter
import com.huzi.shared.moshi.DateAdapter
import com.huzi.shared.moshi.NullPrimitiveAdapter
import com.huzi.shared.retrofit.adapter.FlowCallAdapterFactory
import com.huzi.shared.retrofit.adapter.LiveDataCallAdapterFactory
import com.huzi.shared.retrofit.adapter.SuspendCallAdapterFactory
import com.huzi.shared.retrofit.interceptor.AuthInterceptor
import com.huzi.shared.retrofit.interceptor.CityAuthInterceptor
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    companion object {
        const val WEATHER = "weather"
        const val CITY = "city"
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(): AuthInterceptor =  AuthInterceptor()

    @Provides
    @Singleton
    fun provideCityAuthInterceptor(): CityAuthInterceptor =  CityAuthInterceptor()

    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder {
        val okHttpBuilder = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            val logger = HttpLoggingInterceptor()
            logger.setLevel(HttpLoggingInterceptor.Level.HEADERS)
            logger.setLevel(HttpLoggingInterceptor.Level.BASIC)
            okHttpBuilder.addInterceptor(logger)
            okHttpBuilder.addInterceptor(OkHttpProfilerInterceptor())
        }
        return okHttpBuilder
    }

    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(NullPrimitiveAdapter())
                .add(DateAdapter())
                .add(KotlinJsonAdapterFactory())
                .add(BonusMoshiAdapter())
                .build()
    }

    @Provides
    fun createRetrofitBuilder(moshi: Moshi): Retrofit.Builder {
        return Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .addCallAdapterFactory(FlowCallAdapterFactory())
                .addCallAdapterFactory(SuspendCallAdapterFactory())
    }

    @Provides
    @Singleton
    @Named(WEATHER)
    fun createWeatherRetrofit(retrofit: Retrofit.Builder, okHttpClient: OkHttpClient.Builder, authInterceptor: AuthInterceptor): Retrofit {
        okHttpClient.apply {
            addInterceptor(authInterceptor)
        }
        val client = okHttpClient.build()
        return retrofit
                .client(client)
                .baseUrl(BuildConfig.BASE_URL)
                .build()
    }

    @Provides
    @Singleton
    @Named(CITY)
    fun createCityRetrofit(retrofit: Retrofit.Builder, okHttpClient: OkHttpClient.Builder, interceptor: CityAuthInterceptor): Retrofit {
        okHttpClient.apply {
            addInterceptor(interceptor)
        }
        val client = okHttpClient.build()
        return retrofit
                .client(client)
                .baseUrl(BuildConfig.CITY_SEARCH_URL)
                .build()
    }
}