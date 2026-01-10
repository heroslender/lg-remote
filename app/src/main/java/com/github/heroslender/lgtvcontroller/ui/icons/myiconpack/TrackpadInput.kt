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

val MyIconPack.TrackpadInput: ImageVector
    get() {
        if (_trackpadInput != null) {
            return _trackpadInput!!
        }
        _trackpadInput =
            Builder(
                name = "TrackpadInput",
                defaultWidth = 24.0.dp,
                defaultHeight = 24.0.dp,
                viewportWidth = 960.0f,
                viewportHeight = 960.0f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color(0xFFe3e3e3)),
                        stroke = null,
                        strokeLineWidth = 0.0f,
                        strokeLineCap = Butt,
                        strokeLineJoin = Miter,
                        strokeLineMiter = 4.0f,
                        pathFillType = NonZero,
                    ) {
                        moveTo(537.0f, 901.0f)
                        quadToRelative(-24.35f, 0.0f, -46.68f, -8.79f)
                        quadTo(468.0f, 883.43f, 452.0f, 867.0f)
                        lineTo(247.0f, 661.0f)
                        lineToRelative(34.43f, -32.11f)
                        quadToRelative(14.79f, -14.75f, 32.68f, -19.32f)
                        reflectiveQuadTo(355.0f, 610.0f)
                        lineToRelative(69.0f, 19.0f)
                        verticalLineToRelative(-304.0f)
                        quadToRelative(0.0f, -16.05f, 11.53f, -27.53f)
                        quadTo(447.06f, 286.0f, 463.69f, 286.0f)
                        quadToRelative(16.21f, 0.0f, 28.26f, 11.47f)
                        quadTo(504.0f, 308.95f, 504.0f, 325.0f)
                        verticalLineToRelative(410.0f)
                        lineToRelative(-101.0f, -29.0f)
                        lineToRelative(105.51f, 105.12f)
                        quadToRelative(3.88f, 4.91f, 10.71f, 7.9f)
                        quadTo(526.05f, 822.0f, 537.0f, 822.0f)
                        horizontalLineToRelative(244.0f)
                        quadToRelative(33.45f, 0.0f, 57.22f, -23.79f)
                        quadTo(862.0f, 774.41f, 862.0f, 741.0f)
                        verticalLineToRelative(-151.0f)
                        quadToRelative(0.0f, -16.05f, 11.44f, -27.52f)
                        quadTo(884.88f, 551.0f, 901.06f, 551.0f)
                        quadToRelative(18.19f, 0.0f, 29.06f, 11.48f)
                        quadTo(941.0f, 573.95f, 941.0f, 590.0f)
                        verticalLineToRelative(151.0f)
                        quadToRelative(0.0f, 68.05f, -45.97f, 114.03f)
                        quadTo(849.05f, 901.0f, 781.0f, 901.0f)
                        close()
                        moveTo(571.0f, 614.0f)
                        verticalLineToRelative(-165.79f)
                        quadToRelative(0.0f, -16.91f, 10.89f, -28.06f)
                        reflectiveQuadTo(609.2f, 409.0f)
                        reflectiveQuadToRelative(28.61f, 11.47f)
                        quadTo(650.0f, 431.95f, 650.0f, 448.0f)
                        verticalLineToRelative(166.0f)
                        close()
                        moveTo(716.0f, 614.0f)
                        verticalLineToRelative(-93.0f)
                        quadToRelative(0.0f, -16.05f, 11.53f, -27.52f)
                        quadTo(739.06f, 482.0f, 755.69f, 482.0f)
                        quadToRelative(16.21f, 0.0f, 27.76f, 11.48f)
                        quadTo(795.0f, 504.95f, 795.0f, 521.0f)
                        verticalLineToRelative(93.0f)
                        close()
                        moveTo(781.0f, 822.0f)
                        lineTo(508.0f, 822.0f)
                        close()
                        moveTo(150.0f, 781.0f)
                        quadToRelative(-37.17f, 0.0f, -64.09f, -26.91f)
                        quadTo(59.0f, 727.18f, 59.0f, 690.0f)
                        verticalLineToRelative(-500.0f)
                        quadToRelative(0.0f, -37.59f, 26.91f, -64.79f)
                        quadTo(112.83f, 98.0f, 150.0f, 98.0f)
                        horizontalLineToRelative(620.0f)
                        quadToRelative(37.59f, 0.0f, 64.79f, 27.21f)
                        quadTo(862.0f, 152.41f, 862.0f, 190.0f)
                        verticalLineToRelative(179.0f)
                        horizontalLineToRelative(-92.0f)
                        verticalLineToRelative(-179.0f)
                        lineTo(150.0f, 190.0f)
                        verticalLineToRelative(500.0f)
                        horizontalLineToRelative(55.0f)
                        lineToRelative(92.0f, 91.0f)
                        close()
                    }
                }
                .build()
        return _trackpadInput!!
    }

private var _trackpadInput: ImageVector? = null

@Preview
@Composable
private fun Preview() {
    Box(modifier = Modifier.padding(12.dp)) {
        Image(
            imageVector = MyIconPack.TrackpadInput,
            contentDescription = null,
        )
    }
}
