package com.github.heroslender.lgtvcontroller.ui.icons

import androidx.compose.ui.graphics.vector.ImageVector
import com.github.heroslender.lgtvcontroller.ui.icons.myiconpack.Exclamation
import com.github.heroslender.lgtvcontroller.ui.icons.myiconpack.InfoI
import com.github.heroslender.lgtvcontroller.ui.icons.myiconpack.TvRemote
import kotlin.collections.List as ____KtList

object MyIconPack

private var __AllIcons: ____KtList<ImageVector>? = null

val MyIconPack.AllIcons: ____KtList<ImageVector>
    get() {
        if (__AllIcons != null) {
            return __AllIcons!!
        }
        __AllIcons = listOf(TvRemote, InfoI, Exclamation)
        return __AllIcons!!
    }
