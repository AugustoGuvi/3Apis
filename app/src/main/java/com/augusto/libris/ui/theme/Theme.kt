package com.augusto.libris.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

val Ink = Color(0xFF233348)
val SecondaryInk = Color(0xFF657487)
val Paper = Color(0xFFF9F7F2)
val Rust = Color(0xFFB35E43)
val Honey = Color(0xFFEFC78A)
val Border = Color(0xFFE5E5DF)

private val palette = lightColorScheme(
    primary = Ink,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE4E9EF),
    onPrimaryContainer = Ink,
    secondary = Rust,
    onSecondary = Color.White,
    background = Paper,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    outline = Border,
    surfaceVariant = Color(0xFFF3F0EA)
)

@Composable
fun LibrisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = palette,
        typography = Typography(),
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(18.dp),
            large = RoundedCornerShape(26.dp)
        ),
        content = content
    )
}
