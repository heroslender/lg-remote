package com.github.heroslender.lgtvcontroller.ui.controller

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.github.heroslender.lgtvcontroller.ui.theme.LGTVControllerTheme

val ButtonShape = ShapeDefaults.Large
val ControlsSpacing = 10.dp

@Composable
fun CTextButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonShape,
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
    shape: Shape = ButtonShape,
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
fun CIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonShape,
    useDefaultTint: Boolean = false,
    onClick: () -> Unit = {},
) {
    val color = if (useDefaultTint) Color.Unspecified else null
    CButton(enabled = enabled, shape = shape, modifier = modifier, onClick = onClick) {
        if (color == null) {
            Icon(imageVector = imageVector, contentDescription, modifier = Modifier.size(32.dp))
        } else {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
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
    shape: Shape = ButtonShape,
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
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    topButton: @Composable () -> Unit,
    bottomButton: @Composable () -> Unit,
) {
    Surface(
        shape = ButtonShape,
        color = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
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

@Preview(
    showBackground = true,
)
@Composable
fun PreviewLight() {
    LGTVControllerTheme {
        CTextButton("Hello")
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDark() {
    LGTVControllerTheme {
        CTextButton("Hello")
    }
}

@Preview(
    showBackground = true,
)
@Composable
fun PreviewLightDisabled() {
    LGTVControllerTheme {
        CTextButton("Hello", enabled = false)
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewDarkDisabled() {
    LGTVControllerTheme {
        CTextButton("Hello", enabled = false)
    }
}