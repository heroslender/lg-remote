package com.github.heroslender.lgtvcontroller.storage

import com.github.heroslender.lgtvcontroller.domain.model.Tv
import com.github.heroslender.lgtvcontroller.storage.entity.TvEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class OfflineTvRepository(private val tvDao: TvDao) : TvRepository {
    override fun getAllTvsStream(): Flow<List<Tv>> = tvDao.getAllTvs().map { it.map(TvEntity::toDomain) }

    override fun getTvStream(id: String): Flow<Tv> = tvDao.getTv(id).map(TvEntity::toDomain)

    override suspend fun insertTv(tv: Tv) = tvDao.insert(tv.toEntity())

    override suspend fun updateTv(tv: Tv) = tvDao.update(tv.toEntity())

    override suspend fun deleteTv(tv: Tv) = tvDao.update(tv.toEntity())
}