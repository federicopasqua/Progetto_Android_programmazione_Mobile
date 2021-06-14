package com.example.progetto.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "connections")
data class Connections (

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    var key: String,

    @ColumnInfo(name = "private_key")
    var privateKey: String?,

    @ColumnInfo(name = "last_update")
    var lastUpdate: Long? = null,

)