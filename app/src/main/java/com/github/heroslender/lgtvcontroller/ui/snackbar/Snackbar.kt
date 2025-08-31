package com.github.heroslender.lgtvcontroller.ui.snackbar

class Snackbar(
    val type: Type,
    val title: String,
    val description: String? = null,
    val actionTitle: String? = null,
    val action: (() -> Unit)? = null,
    val duration: StackedSnackbarDuration = StackedSnackbarDuration.Normal,
) {
    companion object{
        fun info(
            title: String,
            description: String? = null,
            actionTitle: String? = null,
            action: (() -> Unit)? = null,
            duration: StackedSnackbarDuration = StackedSnackbarDuration.Short,
        ) = Snackbar(Type.INFO, title, description, actionTitle, action, duration)

        fun warning(
            title: String,
            description: String? = null,
            actionTitle: String? = null,
            action: (() -> Unit)? = null,
            duration: StackedSnackbarDuration = StackedSnackbarDuration.Normal,
        ) = Snackbar(Type.WARNING, title, description, actionTitle, action, duration)

        fun error(
            title: String,
            description: String? = null,
            actionTitle: String? = null,
            action: (() -> Unit)? = null,
            duration: StackedSnackbarDuration = StackedSnackbarDuration.Normal,
        ) = Snackbar(Type.ERROR, title, description, actionTitle, action, duration)

        fun success(
            title: String,
            description: String? = null,
            actionTitle: String? = null,
            action: (() -> Unit)? = null,
            duration: StackedSnackbarDuration = StackedSnackbarDuration.Short,
        ) = Snackbar(Type.SUCCESS, title, description, actionTitle, action, duration)
    }

    enum class Type {
        INFO,
        WARNING,
        ERROR,
        SUCCESS
    }
}

