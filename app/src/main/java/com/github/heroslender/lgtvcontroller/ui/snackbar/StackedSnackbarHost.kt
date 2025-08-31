package com.github.heroslender.lgtvcontroller.ui.snackbar

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StackedSnackbarHost(
    hostState: StackedSnakbarHostState,
    modifier: Modifier = Modifier,
) {
    if (hostState.currentSnackbarData.isEmpty()) {
        return
    }

    StackedSnackbar(
        snackbarData = hostState.currentSnackbarData.toList(),
        onSnackbarRemoved = {
            hostState.currentSnackbarData = hostState.currentSnackbarData.toMutableList().apply {
                remove(it)
            }
        },
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 60.dp)
            .then(modifier),
    )
}

@Stable
class StackedSnakbarHostState(
    val maxStack: Int = Int.MAX_VALUE,
) {
    internal var currentSnackbarData by mutableStateOf<List<StackedSnackbarData>>(
        listOf(
        )
    )

    fun showSnackbar(
        snackbar: Snackbar,
    ) {
        when (snackbar.type) {
            Snackbar.Type.INFO -> showInfoSnackbar(
                title = snackbar.title,
                description = snackbar.description,
                actionTitle = snackbar.actionTitle,
                action = snackbar.action,
                duration = snackbar.duration,
            )
            Snackbar.Type.SUCCESS -> showSuccessSnackbar(
                title = snackbar.title,
                description = snackbar.description,
                actionTitle = snackbar.actionTitle,
                action = snackbar.action,
                duration = snackbar.duration,
            )
            Snackbar.Type.WARNING -> showWarningSnackbar(
                title = snackbar.title,
                description = snackbar.description,
                actionTitle = snackbar.actionTitle,
                action = snackbar.action,
                duration = snackbar.duration,
            )
            Snackbar.Type.ERROR -> showErrorSnackbar(
                title = snackbar.title,
                description = snackbar.description,
                actionTitle = snackbar.actionTitle,
                action = snackbar.action,
                duration = snackbar.duration,
            )

        }
    }

    fun showInfoSnackbar(
        title: String,
        description: String? = null,
        actionTitle: String? = null,
        action: (() -> Unit)? = null,
        duration: StackedSnackbarDuration = StackedSnackbarDuration.Short,
    ) {
        showSnackbar(
            data =
                StackedSnackbarData.Normal(
                    Type.Info,
                    title,
                    description,
                    actionTitle,
                    action,
                    duration,
                ),
        )
    }

    fun showSuccessSnackbar(
        title: String,
        description: String? = null,
        actionTitle: String? = null,
        action: (() -> Unit)? = null,
        duration: StackedSnackbarDuration = StackedSnackbarDuration.Short,
    ) {
        showSnackbar(
            data =
                StackedSnackbarData.Normal(
                    Type.Success,
                    title,
                    description,
                    actionTitle,
                    action,
                    duration,
                ),
        )
    }

    fun showWarningSnackbar(
        title: String,
        description: String? = null,
        actionTitle: String? = null,
        action: (() -> Unit)? = null,
        duration: StackedSnackbarDuration = StackedSnackbarDuration.Normal,
    ) {
        showSnackbar(
            data =
                StackedSnackbarData.Normal(
                    Type.Warning,
                    title,
                    description,
                    actionTitle,
                    action,
                    duration,
                ),
        )
    }

    fun showErrorSnackbar(
        title: String,
        description: String? = null,
        actionTitle: String? = null,
        action: (() -> Unit)? = null,
        duration: StackedSnackbarDuration = StackedSnackbarDuration.Normal,
    ) {
        showSnackbar(
            data =
                StackedSnackbarData.Normal(
                    Type.Error,
                    title,
                    description,
                    actionTitle,
                    action,
                    duration,
                ),
        )
    }

    fun showCustomSnackbar(
        content: @Composable (() -> Unit) -> Unit,
        duration: StackedSnackbarDuration = StackedSnackbarDuration.Indefinite,
    ) {
        showSnackbar(
            data = StackedSnackbarData.Custom(content, duration),
        )
    }

    private fun showSnackbar(data: StackedSnackbarData) {
        currentSnackbarData = currentSnackbarData.toMutableList().apply {
            add(data)
        }
    }
}

fun StackedSnackbarDuration.toMillis(): Long =
    when (this) {
        StackedSnackbarDuration.Short -> 4000L
        StackedSnackbarDuration.Normal -> 7000L
        StackedSnackbarDuration.Long -> 10000L
        StackedSnackbarDuration.Indefinite -> Long.MAX_VALUE
    }