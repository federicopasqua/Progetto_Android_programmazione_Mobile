package com.example.progetto.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RewriteQueriesToDropUnusedColumns


@Entity(tableName = "coins")
data class Coins (

    @ColumnInfo(name = "name")
    val name: String,

    @PrimaryKey()
    @ColumnInfo(name = "ticker")
    var ticker: String,

    @ColumnInfo(name = "price")
    var price: Double?,

    @ColumnInfo(name = "change1h")
    var change1h: Double?,

    @ColumnInfo(name = "change24h")
    var change24h: Double?,

    @ColumnInfo(name = "change7d")
    var change7d: Double?,

    @ColumnInfo(name = "market_cap")
    var marketCap: Double?,

    @ColumnInfo(name = "rank")
    var rank: Int?,

    @ColumnInfo(name = "high")
    var high: Double?,

    @ColumnInfo(name = "low")
    var low: Double?,

    var supply: Double?,

    @ColumnInfo(name = "volume")
    var volume: Double?,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image: ByteArray? = null,

    @ColumnInfo(name = "last_modified")
    var lastModified: String? = null,
)

//Tipo speciale per Coin con l'aggiunta del balance preso dalla somma di tutte le transazioni
data class coinsWithBalance (
    val name: String,
    var ticker: String,
    var price: Double?,
    var change1h: Double?,
    var change24h: Double?,
    var change7d: Double?,
    @ColumnInfo(name = "market_cap")
    var marketCap: Double?,
    @ColumnInfo(name = "rank")
    var rank: Int?,

    @ColumnInfo(name = "high")
    var high: Double?,

    @ColumnInfo(name = "low")
    var low: Double?,

    var supply: Double?,
    var volume: Double?,
    var image: ByteArray? = null,
    @ColumnInfo(name = "last_modified")
    var lastModified: String? = null,
    var balance: Float
)






