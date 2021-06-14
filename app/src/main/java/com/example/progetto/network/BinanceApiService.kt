package com.example.progetto.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

class Binance(val key: String, val secret: String) {


    private  val BASE_URL = "https://api.binance.com/"

    val retrofitService: BinanceApiService by lazy {
        retrofit.create(BinanceApiService::class.java)
    }

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val httpClient = OkHttpClient.Builder()
        //Interceptor aggiunto per questi endpoint richiedono che il body sia frimato dalla privatekey
        .addNetworkInterceptor(AuthenticationInterceptor(key, secret))
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(httpClient)
        .baseUrl(BASE_URL)
        .build()

    interface BinanceApiService {


        @GET("api/v3/myTrades")
        suspend fun trades(
            @Query("symbol") symbol: String,
            @Query("limit") limit: Int? = null,
            @Query("fromId") fromId: Long? = null,
            @Query("recvWindow") receivingWindow: Long = 60000,
            @Query("timestamp") timestamp: Long = System.currentTimeMillis()
        ): List<Trade>

        @GET("/sapi/v1/capital/deposit/hisrec")
        suspend fun depositHistory(
            @Query("coin") asset: String? = null,
            @Query("status") status: Int? = null,
            @Query("startTime") startTime: Long? = null,
            @Query("endTime") endTime: Long? = null,
            @Query("recvWindow") recvWindow: Long = 60000,
            @Query("timestamp") timestamp: Long = System.currentTimeMillis()
        )
                : List<Deposit>

        @GET("/sapi/v1/capital/withdraw/history")
        suspend fun withdrawHistory(
            @Query("coin") asset: String? = null,
            @Query("status") status: Int? = null,
            @Query("startTime") startTime: Long? = null,
            @Query("endTime") endTime: Long? = null,
            @Query("recvWindow") recvWindow: Long = 60000,
            @Query("timestamp") timestamp: Long = System.currentTimeMillis()
        )
                : List<Withdraw>

    }

}