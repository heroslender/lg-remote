package com.github.heroslender.lgtvcontroller.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TvDao {

    @Query("SELECT * FROM tvs WHERE id = :id")
    fun getTv(id: String): Flow<Tv>

    @Query("SELECT * FROM tvs")
    fun getAllTvs(): Flow<List<Tv>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tv: Tv)

    @Update
    suspend fun update(tv: Tv)

    @Delete
    suspend fun delete(tv: Tv)
}