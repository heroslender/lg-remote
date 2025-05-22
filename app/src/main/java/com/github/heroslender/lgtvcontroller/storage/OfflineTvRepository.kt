package com.github.heroslender.lgtvcontroller.storage

import kotlinx.coroutines.flow.Flow

class OfflineTvRepository(private val tvDao: TvDao) : TvRepository {
    override fun getAllTvsStream(): Flow<List<Tv>> = tvDao.getAllTvs()

    override fun getTvStream(id: String): Flow<Tv> = tvDao.getTv(id)

    override suspend fun insertTv(tv: Tv) = tvDao.insert(tv)

    override suspend fun updateTv(tv: Tv) = tvDao.update(tv)

    override suspend fun deleteTv(tv: Tv) = tvDao.update(tv)
}