package com.github.heroslender.lgtvcontroller.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.heroslender.lgtvcontroller.R.string
import com.github.heroslender.lgtvcontroller.ui.snackbar.Snackbar
import com.github.heroslender.lgtvcontroller.ui.snackbar.StackedSnackbarHost
import com.github.heroslender.lgtvcontroller.ui.snackbar.rememberStackedSnackbarHostState
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectedDeviceScaffold(
    errorFlow: Flow<Snackbar>,
    textInputState: TvTextInputState,
    topBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val stackedSnackbarHostState = rememberStackedSnackbarHostState()

    LaunchedEffect(errorFlow) {
        errorFlow.collect { snackbar ->
            stackedSnackbarHostState.showSnackbar(snackbar)
        }
    }

    var isTextSet by remember { mutableStateOf<String?>(null) }
    if (isTextSet != null) {
        stackedSnackbarHostState.showSnackbar(
            Snackbar.success(
                title = stringResource(string.text_input_dialog_success_title),
                description = stringResource(
                    string.text_input_dialog_success_description,
                    isTextSet ?: ""
                )
            )
        )
        isTextSet = null
    }

    var showKeyboardTextInputDialog by remember { mutableStateOf(false) }
    if (showKeyboardTextInputDialog) {
        TextInputDialog(
            title = stringResource(string.text_input_dialog_title),
            onBackspace = { textInputState.sendBackspace() },
            onEnter = { textInputState.sendEnter() },
            onConfirmation = {
                textInputState.sendText(it)
                isTextSet = it
                showKeyboardTextInputDialog = false
            },
            onDismissRequest = {
                showKeyboardTextInputDialog = false
            },
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = topBar,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AnimatedVisibility(textInputState.isKeyboardOpen) {
                TextInputBottomBar(onClick = {
                    showKeyboardTextInputDialog = true
                })
            }
        },
        snackbarHost = { StackedSnackbarHost(hostState = stackedSnackbarHostState) },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            content()
        }
    }
}

@Composable
fun TextInputBottomBar(
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick,
            )
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Filled.Keyboard, null)
        Text("Text Input", modifier = Modifier.padding(start = 8.dp))
    }
}