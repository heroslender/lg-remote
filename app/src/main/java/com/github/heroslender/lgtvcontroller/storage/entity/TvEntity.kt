package com.github.heroslender.lgtvcontroller.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tvs")
data class TvEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    var displayName: String?,
    var appList: AppList,
    var inputList: InputList,
)