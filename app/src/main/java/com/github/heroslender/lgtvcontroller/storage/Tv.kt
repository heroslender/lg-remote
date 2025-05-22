package com.github.heroslender.lgtvcontroller.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tvs")
data class Tv(
    @PrimaryKey
    val id: String,
    val name: String,
    var displayName: String,
)