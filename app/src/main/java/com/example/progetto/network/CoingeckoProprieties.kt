package com.example.progetto.network

import com.squareup.moshi.Json

data class coinsProperty(
    val id: String,
    val symbol: String,
    val name: String,
    val current_price: Double?,
    val market_cap_rank: Int?,
    val market_cap: Double?,
    @Json(name="price_change_percentage_1h_in_currency") val change1h: Double?,
    @Json(name="price_change_percentage_24h_in_currency") val change24h: Double?,
    @Json(name="price_change_percentage_7d_in_currency") val change7d: Double?,
    val total_volume: Double?,
    val high_24h: Double?,
    val low_24h: Double?,
    val circulating_supply: Double?,
    @Json(name="image") val imgSrcUrl: String?
)

/*
data class coinDetails(
    val id: String,
    val symbol: String,
    val name: String,
    val description: description,
    val image: images,
    @Json(name="market_cap_rank") val rank: Int,
    val market_data: marketData,
)

data class description (
    val en: String,
)

data class images (
    val small: String,
)

data class marketData (
    val current_price: price,
    val market_cap: marketCap,
    val total_volume: volume,
    @Json(name="price_change_percentage_24h") val change24h: Float,
)


data class price(
    val eur: Float,
)

data class marketCap(
    val eur: String,
)

data class volume(
    val eur: String,
)
*/