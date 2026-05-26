package theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColors = lightColors(
    primary = Primary,
    primaryVariant = Primary,
    secondary = Secondary,
    background = Surface,
    surface = Surface,
    error = Error,
    onPrimary = OnPrimary,
    onSecondary = OnPrimary,
    onBackground = OnSurface,
    onSurface = OnSurface,
    onError = OnPrimary
)

val AppShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(4.dp)
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = LightColors,
        shapes = AppShapes,
        content = content
    )
}
