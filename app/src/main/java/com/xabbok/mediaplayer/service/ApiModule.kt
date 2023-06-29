package com.xabbok.mediaplayer.service

import com.xabbok.mediaplayer.BuildConfig
import com.xabbok.mediaplayer.error.ApiAppError
import com.xabbok.mediaplayer.error.NetworkAppError
import com.xabbok.mediaplayer.error.UnknownAppError
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {
    @Provides
    @Singleton
    fun provideLogging(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .let {
            if (BuildConfig.DEBUG) {
                it.addInterceptor(logging)
            } else
                it
        }
        .addInterceptor(Interceptor { chain ->
            val request: Request = chain.request()

            val response: Response = try {
                chain.proceed(request)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                throw NetworkAppError
            } catch (e: Exception) {
                throw UnknownAppError
            }

            if (!response.isSuccessful) {
                response.close()
                throw ApiAppError(response.code, response.message)
            }

            response
        })
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ): ApiService = retrofit.create(ApiService::class.java)
}