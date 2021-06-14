package com.example.progetto.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "http://65.21.189.117:4003/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ArkApiService {

    @GET("wallets/{address}/transactions")
    suspend fun getTransactions(@Path("address") address: String, @Query("orderBy") orderBy: String = "timestamp:asc"): arkTransactions

}


object ArkApi {
    val retrofitService: ArkApiService by lazy {
        retrofit.create(ArkApiService::class.java)
    }
}