package com.github.heroslender.lgtvcontroller.storage

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.github.heroslender.lgtvcontroller.storage.entity.TvEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TvDao {

    @Query("SELECT * FROM tvs WHERE id = :id")
    fun getTv(id: String): Flow<TvEntity>

    @Query("SELECT * FROM tvs")
    fun getAllTvs(): Flow<List<TvEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tv: TvEntity)

    @Update
    suspend fun update(tv: TvEntity)

    @Delete
    suspend fun delete(tv: TvEntity)
}