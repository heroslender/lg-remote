package com.github.heroslender.lgtvcontroller.ui.snackbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.heroslender.lgtvcontroller.ui.icons.myiconpack.InfoI
import com.github.heroslender.lgtvcontroller.ui.icons.MyIconPack
import com.github.heroslender.lgtvcontroller.ui.icons.myiconpack.Exclamation

@Composable
fun rememberStackedSnackbarHostState(
    maxStack: Int = Int.MAX_VALUE,
): StackedSnakbarHostState = remember {
    StackedSnakbarHostState(maxStack = maxStack)
}

@Stable
internal sealed class StackedSnackbarData(val showDuration: StackedSnackbarDuration) {
    data class Normal(
        val type: Type,
        val title: String,
        val description: String? = null,
        val actionTitle: String? = null,
        val action: (() -> Unit)? = null,
        val duration: StackedSnackbarDuration = StackedSnackbarDuration.Short,
    ) : StackedSnackbarData(duration)

    data class Custom(
        val content: @Composable (() -> Unit) -> Unit,
        val duration: StackedSnackbarDuration = StackedSnackbarDuration.Short,
    ) : StackedSnackbarData(duration)
}

@Stable
internal enum class Type(val icon: ImageVector, val color: Color) {
    Info(MyIconPack.InfoI, SnackbarColor.Info),
    Warning(MyIconPack.Exclamation, SnackbarColor.Warning),
    Error(MyIconPack.Exclamation, SnackbarColor.Error),
    Success(Icons.Filled.Check, SnackbarColor.Success),
}

object SnackbarColor {
    val Link = Color(0xFF246EE5)
    val Info = Color(0xFF3150EC)
    val Warning = Color(0xFFFE9E01)
    val Error = Color(0xFFF54F4E)
    val Success = Color(0xFF24BF5F)
}

@Stable
enum class StackedSnackbarDuration {
    Short,
    Normal,
    Long,
    Indefinite,
}

internal object Constant {
    const val OFFSET_THRESHOLD_EXIT_LEFT = -350
    const val OFFSET_THRESHOLD_EXIT_RIGHT = 350
}