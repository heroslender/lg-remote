package com.github.heroslender.lgtvcontroller.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.automirrored.outlined.KeyboardReturn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.heroslender.lgtvcontroller.ui.preferences.DialogTextField
import com.github.heroslender.lgtvcontroller.ui.preferences.InputValidator

@Composable
fun TextInputDialog(
    title: String,
    value: String = "",
    inputValidation: InputValidator? = null,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
    onConfirmation: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                val focusRequester = remember { FocusRequester() }
                var textFieldvalue by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = value,
                            selection = TextRange(value.length),
                        )
                    )
                }

                val errorMsg = remember(textFieldvalue) {
                    if (inputValidation != null) {
                        inputValidation(textFieldvalue.text)
                    } else {
                        null
                    }
                }

                val isError = errorMsg != null
                AnimatedVisibility(isError) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .fillMaxHeight()
                                .height(1.dp)
                        )

                        Text(
                            text = errorMsg ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }

                DialogTextField(
                    value = textFieldvalue,
                    onValueChange = { textFieldvalue = it },
                    placeholder = title,
                    isError = isError,
                    singleLine = true,
                    modifier = Modifier
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.titleMedium,
                    trailingIcon = {
                        Row {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.Backspace,
                                contentDescription = "Press Backspace",
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = null,
                                        indication = ripple(),
                                        onClick = {
                                            onBackspace()
                                        }
                                    )
                                    .padding(end = 8.dp)
                            )

                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardReturn,
                                contentDescription = "Press Enter",
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = null,
                                        indication = ripple(),
                                        onClick = {
                                            onEnter()
                                        }
                                    )
                                    .padding(end = 8.dp)
                            )
                        }
                    },
                )

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                    ) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = { if (!isError) onConfirmation(textFieldvalue.text) },
                    ) {
                        Text("Set")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextDialogPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(15.dp, 10.dp)
    ) {
        TextInputDialog(
            title = "Set text input",
            value = "You are awesome!",
            onBackspace = { },
            onEnter = { },
            onConfirmation = { },
            onDismissRequest = { },
        )
    }
}
