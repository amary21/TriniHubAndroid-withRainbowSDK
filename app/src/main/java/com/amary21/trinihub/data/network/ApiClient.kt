package com.amary21.trinihub.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val BASE_URL = "https://sandbox.openrainbow.com/"
const val BASE_URL_HUB = "https://trini-hub.000webhostapp.com/"
const val APP_ID = "0ae77d403ff211ea99fa4daad3b6a28a"
const val APP_SECRET_KEY = "lYjaJFCwOjjg76WSUdpXoBKFafbIwokQvEydoyOo65VMzvfh9DfUXBpzOVO1Av7u"
const val QR_CODE_API ="https://api.qrserver.com/v1/create-qr-code/?size=500x500&data="

object ApiClient {

    private val builder = Retrofit.Builder()
    private val client = OkHttpClient.Builder()
        .addInterceptor(run {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        })
        .build()

    private fun retrofit() : Retrofit{
        return builder
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun retrofitHub() : Retrofit{
        return builder
            .baseUrl(BASE_URL_HUB)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getClient() : ApiInterface{
        return retrofit().create(ApiInterface::class.java)
    }

    fun getClientHub() : ApiInterface{
        return retrofitHub().create(ApiInterface::class.java)
    }
}