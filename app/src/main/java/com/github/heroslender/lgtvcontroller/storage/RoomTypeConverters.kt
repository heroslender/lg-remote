package com.github.heroslender.lgtvcontroller.storage

import androidx.room.TypeConverter
import com.github.heroslender.lgtvcontroller.storage.entity.AppList
import com.google.gson.Gson

class RoomTypeConverters{
    @TypeConverter
    fun convertAppListToJSONString(appList: AppList): String = Gson().toJson(appList)
    @TypeConverter
    fun convertJSONStringToAppList(jsonString: String): AppList = Gson().fromJson(jsonString,AppList::class.java)

}