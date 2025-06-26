package com.github.heroslender.lgtvcontroller.ui.icons

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

val TvRemote: ImageVector
    get() {
        if (_tvRemote != null) {
            return _tvRemote!!
        }

        _tvRemote = Builder(
            name = "TvRemote",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 960.0f,
            viewportHeight = 960.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFe3e3e3)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(360.0f, 840.0f)
                horizontalLineToRelative(240.0f)
                verticalLineToRelative(-200.0f)
                quadToRelative(-25.0f, 19.0f, -55.5f, 29.5f)
                reflectiveQuadTo(480.0f, 680.0f)
                quadToRelative(-34.0f, 0.0f, -64.5f, -10.5f)
                reflectiveQuadTo(360.0f, 640.0f)
                verticalLineToRelative(200.0f)
                close()
                moveTo(480.0f, 600.0f)
                quadToRelative(50.0f, 0.0f, 85.0f, -35.0f)
                reflectiveQuadToRelative(35.0f, -85.0f)
                quadToRelative(0.0f, -50.0f, -35.0f, -85.0f)
                reflectiveQuadToRelative(-85.0f, -35.0f)
                quadToRelative(-50.0f, 0.0f, -85.0f, 35.0f)
                reflectiveQuadToRelative(-35.0f, 85.0f)
                quadToRelative(0.0f, 50.0f, 35.0f, 85.0f)
                reflectiveQuadToRelative(85.0f, 35.0f)
                close()
                moveTo(360.0f, 320.0f)
                quadToRelative(25.0f, -19.0f, 55.5f, -29.5f)
                reflectiveQuadTo(480.0f, 280.0f)
                quadToRelative(34.0f, 0.0f, 64.5f, 10.5f)
                reflectiveQuadTo(600.0f, 320.0f)
                verticalLineToRelative(-200.0f)
                lineTo(360.0f, 120.0f)
                verticalLineToRelative(200.0f)
                close()
                moveTo(360.0f, 920.0f)
                quadToRelative(-33.0f, 0.0f, -56.5f, -23.5f)
                reflectiveQuadTo(280.0f, 840.0f)
                verticalLineToRelative(-720.0f)
                quadToRelative(0.0f, -33.0f, 23.5f, -56.5f)
                reflectiveQuadTo(360.0f, 40.0f)
                horizontalLineToRelative(240.0f)
                quadToRelative(33.0f, 0.0f, 56.5f, 23.5f)
                reflectiveQuadTo(680.0f, 120.0f)
                verticalLineToRelative(720.0f)
                quadToRelative(0.0f, 33.0f, -23.5f, 56.5f)
                reflectiveQuadTo(600.0f, 920.0f)
                lineTo(360.0f, 920.0f)
                close()
                moveTo(480.0f, 540.0f)
                quadToRelative(-25.0f, 0.0f, -42.5f, -17.5f)
                reflectiveQuadTo(420.0f, 480.0f)
                quadToRelative(0.0f, -25.0f, 17.5f, -42.5f)
                reflectiveQuadTo(480.0f, 420.0f)
                quadToRelative(25.0f, 0.0f, 42.5f, 17.5f)
                reflectiveQuadTo(540.0f, 480.0f)
                quadToRelative(0.0f, 25.0f, -17.5f, 42.5f)
                reflectiveQuadTo(480.0f, 540.0f)
                close()
                moveTo(480.0f, 240.0f)
                quadToRelative(17.0f, 0.0f, 28.5f, -11.5f)
                reflectiveQuadTo(520.0f, 200.0f)
                quadToRelative(0.0f, -17.0f, -11.5f, -28.5f)
                reflectiveQuadTo(480.0f, 160.0f)
                quadToRelative(-17.0f, 0.0f, -28.5f, 11.5f)
                reflectiveQuadTo(440.0f, 200.0f)
                quadToRelative(0.0f, 17.0f, 11.5f, 28.5f)
                reflectiveQuadTo(480.0f, 240.0f)
                close()
                moveTo(480.0f, 680.0f)
                close()
                moveTo(480.0f, 280.0f)
                close()
            }
        }.build()
        return _tvRemote!!
    }

private var _tvRemote: ImageVector? = null

@Preview
@Composable
private fun Preview(): Unit {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(imageVector = TvRemote, contentDescription = "")
    }
}