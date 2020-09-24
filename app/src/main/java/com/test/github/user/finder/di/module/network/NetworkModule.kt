package com.test.github.user.finder.di.module.network

import com.ashokvarma.gander.GanderInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.test.github.user.finder.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import com.test.github.user.finder.datamodule.service.user.UserService

val networkModule = module {

    single<Interceptor>(named("InterceptorUserFinder")) {
        HttpLoggingInterceptor().also { interceptor ->
            interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
    }

    // comment gander interceptor first before start the unit test of api
    factory(named("OkhttpclientUserFinder")) {
        OkHttpClient.Builder().apply {
            HttpLoggingInterceptor().also { interceptor ->
                interceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
                addInterceptor(interceptor)
            }
            addInterceptor(GanderInterceptor(androidContext()).showNotification(true))
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
        }.build()
    }

    single<Retrofit>(named("RetrofitUserFinder")) {
        Retrofit.Builder().apply {
            client(get(named("OkhttpclientUserFinder")))
            baseUrl(BuildConfig.API_BASE_URL)
            addConverterFactory(MoshiConverterFactory.create())
            addCallAdapterFactory(CoroutineCallAdapterFactory())
        }.build()
    }

    factory { get<Retrofit>(named("RetrofitUserFinder")).create(UserService::class.java) }

}
