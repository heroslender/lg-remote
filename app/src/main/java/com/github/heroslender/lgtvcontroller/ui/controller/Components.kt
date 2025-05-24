package com.github.heroslender.lgtvcontroller.ui.controller

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

@Composable
fun CTextButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ShapeDefaults.ExtraSmall,
    fontSize: TextUnit = 3.7.em,
    onClick: () -> Unit = {},
) {
    CButton(enabled = enabled, shape = shape, modifier = modifier, onClick = onClick) {
        Text(text, fontSize = fontSize)
    }
}

@Composable
fun CIconButton(
    iconId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ShapeDefaults.ExtraSmall,
    useDefaultTint: Boolean = false,
    onClick: () -> Unit = {},
) {
    val color = if (useDefaultTint) Color.Unspecified else null
    CButton(enabled = enabled, shape = shape, modifier = modifier, onClick = onClick) {
        if (color == null) {
            Icon(painterResource(iconId), contentDescription, modifier = Modifier.size(32.dp))
        } else {
            Icon(
                painterResource(iconId),
                contentDescription,
                modifier = Modifier.size(32.dp),
                tint = color
            )
        }
    }
}

@Composable
fun CButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ShapeDefaults.ExtraSmall,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        contentPadding = contentPadding,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier,
        content = content
    )
}

@Composable
fun RowScope.VerticalControls(
    centerText: String,
    enabled: Boolean = true,
    topButton: @Composable () -> Unit,
    bottomButton: @Composable () -> Unit,
) {
    Surface(
        shape = ShapeDefaults.ExtraSmall,
        color = if (enabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxHeight()
            .weight(1F)
    ) {
        Column {
            topButton()

            Text(
                text = centerText,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 3.em,
            )

            bottomButton()
        }
    }
}