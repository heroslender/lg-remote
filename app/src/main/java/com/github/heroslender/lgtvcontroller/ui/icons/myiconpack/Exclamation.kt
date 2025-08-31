package com.github.heroslender.lgtvcontroller.ui.icons.myiconpack

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.heroslender.lgtvcontroller.ui.icons.MyIconPack

val MyIconPack.Exclamation: ImageVector
    get() {
        if (_exclamation != null) {
            return _exclamation!!
        }
        _exclamation = Builder(
            name =
                "Exclamation", defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp, viewportWidth = 960.0f, viewportHeight = 960.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFe3e3e3)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(480.0f, 840.0f)
                quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
                reflectiveQuadTo(400.0f, 760.0f)
                quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
                reflectiveQuadTo(480.0f, 680.0f)
                quadToRelative(33.0f, 0.0f, 56.5f, 23.5f)
                reflectiveQuadTo(560.0f, 760.0f)
                quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
                reflectiveQuadTo(480.0f, 840.0f)
                close()
                moveTo(400.0f, 600.0f)
                verticalLineToRelative(-480.0f)
                horizontalLineToRelative(160.0f)
                verticalLineToRelative(480.0f)
                lineTo(400.0f, 600.0f)
                close()
            }
        }
            .build()
        return _exclamation!!
    }

private var _exclamation: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(
            imageVector = MyIconPack.Exclamation,
            contentDescription = ""
        )
    }
}
