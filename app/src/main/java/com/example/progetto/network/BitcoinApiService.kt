package com.example.progetto.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL = "https://blockchain.info/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface BitcoinApiService {

    @GET("rawaddr/{address}")
    suspend fun getTransactions(@Path("address") address: String, @Query("offset") offset: Int = 0, @Query("limit") limit: Int = 50): bitcoinAddress

}


object BitcoinApi {
    val retrofitService: BitcoinApiService by lazy {
        retrofit.create(BitcoinApiService::class.java)
    }
}

