package com.github.heroslender.lgtvcontroller.ui.preferences

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

typealias InputValidator = (String) -> String?

@Composable
fun TextPrefence(
    title: String,
    value: String,
    openDialog: Boolean = false,
    onValueChange: ((String) -> Unit)? = null,
    inputValidation: InputValidator? = null,
) {
    var showDialog by remember { mutableStateOf(openDialog) }
    if (showDialog) {
        TextInputDialog(
            title = title,
            value = value,
            inputValidation = inputValidation,
            onConfirmation = {
                onValueChange?.invoke(it)
                showDialog = false
            },
            onDismissRequest = {
                showDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                enabled = onValueChange != null,
                onClick = {
                    showDialog = true
                },
            )
    ) {
        val titleColor = if (onValueChange != null) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
        }

        Text(
            text = title,
            color = titleColor,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun TextInputDialog(
    title: String,
    value: String,
    inputValidation: InputValidator? = null,
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
                            text = errorMsg?: "",
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
                    textStyle = MaterialTheme.typography.titleMedium
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
                        Text("OK")
                    }
                }
            }
        }
    }
}

@Composable
fun DialogTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    singleLine: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    colors: TextFieldColors = TextFieldDefaults.colors(),
) {
    val interactionSource = remember { MutableInteractionSource() }

    val placeholder = placeholder?.run { @Composable { Text(this) } }
    val textColor = textStyle.color.takeOrElse { colors.textColor(enabled, isError, true) }

    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = modifier.fillMaxWidth(),
        visualTransformation = VisualTransformation.None,
        interactionSource = interactionSource,
        enabled = enabled,
        textStyle = textStyle.copy(color = textColor),
        singleLine = singleLine,
        cursorBrush = SolidColor(colors.cursorColor(isError)),
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            placeholder = placeholder,
            value = value.text,
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            singleLine = singleLine,
            enabled = enabled,
            isError = isError,
            interactionSource = interactionSource,
            contentPadding = PaddingValues(0.dp), // this is how you can remove the padding
            colors = colors,
        )
    }
}

fun TextFieldColors.textColor(enabled: Boolean, isError: Boolean, focused: Boolean): Color =
    when {
        !enabled -> disabledTextColor
        isError -> errorTextColor
        focused -> focusedTextColor
        else -> unfocusedTextColor
    }

fun TextFieldColors.cursorColor(isError: Boolean): Color =
    if (isError) errorCursorColor else cursorColor

@Preview(showBackground = true)
@Composable
fun TextPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp, 10.dp)
    ) {
        TextPrefence(
            title = "Readonly text",
            value = "Some readonly value here",
        )

        TextPrefence(
            title = "Editable text",
            value = "You are awesome!",
            onValueChange = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TextDialogErrorPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(15.dp, 10.dp)
    ) {
        TextPrefence(
            title = "Editable text",
            value = "  ",
            openDialog = true,
            inputValidation = { str ->
                if (str.isBlank()) {
                    "Input cannot be blank"
                } else null
            },
            onValueChange = { }
        )
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
        TextPrefence(
            title = "Editable text",
            value = "You are awesome!",
            openDialog = true,
            onValueChange = { }
        )
    }
}