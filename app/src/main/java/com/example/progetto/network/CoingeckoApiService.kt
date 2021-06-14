package com.example.progetto.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


private const val BASE_URL = "https://api.coingecko.com/api/v3/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

private val imageRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .build()

interface CoingeckoApiService {

    @GET("coins/list")
    suspend fun getCoinsList(): List<coinsProperty>

    @GET("coins/markets")
    suspend fun getDetailedCoinList(@Query("page") page: Int = 1, @Query("per_page") perPage: Int = 50,  @Query("vs_currency") currency: String = "eur", @Query("price_change_percentage") price_change_percentage: String = "1h,24h,7d", @Query("ids") ids: String? = null): List<coinsProperty>


}

interface CoingeckoImageService {

    @Streaming
    @GET()
    suspend fun getImage(@Url url: String, @Header("If-Modified-Since") lastModified: String ): Response<ResponseBody>
}

object CoingeckoApi {
    val retrofitService: CoingeckoApiService by lazy {
        retrofit.create(CoingeckoApiService::class.java)
    }
}

object CoingeckoImage {
    val imageRetrofitService: CoingeckoImageService by lazy {
        imageRetrofit.create(CoingeckoImageService::class.java)
    }
}