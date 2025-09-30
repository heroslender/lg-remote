package com.github.heroslender.lgtvcontroller.storage

import com.github.heroslender.lgtvcontroller.domain.model.Tv
import com.github.heroslender.lgtvcontroller.storage.entity.AppList
import com.github.heroslender.lgtvcontroller.storage.entity.InputList
import com.github.heroslender.lgtvcontroller.storage.entity.TvEntity

fun TvEntity.toDomain(): Tv {
    return Tv(
        id = id,
        name = name,
        displayName = displayName,
        autoConnect = autoConnect,
        apps = appList.apps,
        inputs = inputList.apps,
    )
}

fun Tv.toEntity(): TvEntity {
    return TvEntity(
        id = id,
        name = name,
        displayName = displayName,
        autoConnect = autoConnect,
        appList = AppList(apps),
        inputList = InputList(inputs)
    )
}