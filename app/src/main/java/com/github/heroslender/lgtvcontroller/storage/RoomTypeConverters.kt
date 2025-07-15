package com.github.heroslender.lgtvcontroller.storage

import androidx.room.TypeConverter
import com.github.heroslender.lgtvcontroller.storage.entity.AppList
import com.github.heroslender.lgtvcontroller.storage.entity.InputList
import com.google.gson.Gson

class RoomTypeConverters{
    @TypeConverter
    fun convertAppListToJSONString(appList: AppList): String = Gson().toJson(appList)
    @TypeConverter
    fun convertJSONStringToAppList(jsonString: String): AppList = Gson().fromJson(jsonString,AppList::class.java)

    @TypeConverter
    fun convertInputListToJSONString(inputList: InputList): String = Gson().toJson(inputList)
    @TypeConverter
    fun convertJSONStringToInputList(jsonString: String): InputList = Gson().fromJson(jsonString,InputList::class.java)

}