package com.example.newapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.appBackground(): Modifier = this.background(
    brush = Brush.verticalGradient(
        colors = listOf(BackgroundDark, BackgroundLight)
    )
)

fun Modifier.primaryGradient(): Modifier = this.background(
    brush = Brush.horizontalGradient(
        colors = listOf(AccentBlue, AccentTeal)
    )
)

fun Modifier.cardStyle(): Modifier = this
    .background(CardBackground, shape = RoundedCornerShape(16.dp))
    .clip(RoundedCornerShape(16.dp))

fun Modifier.glassEffect(): Modifier = this
    .background(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(24.dp)
    )

fun Modifier.glassCard(): Modifier = this
    .background(
        color = CardBackground.copy(alpha = 0.5f),
        shape = RoundedCornerShape(24.dp)
    )
    .clip(RoundedCornerShape(24.dp))

fun Modifier.premiumCard(borderColor: Color = Color.White.copy(alpha = 0.1f)): Modifier = this
    .background(
        color = CardBackground.copy(alpha = 0.6f),
        shape = RoundedCornerShape(24.dp)
    )
    .border(1.dp, borderColor, RoundedCornerShape(24.dp))
    .clip(RoundedCornerShape(24.dp))

fun Modifier.secondaryButton(): Modifier = this
    .fillMaxWidth()
    .height(56.dp)
    .background(ButtonSecondary, shape = RoundedCornerShape(16.dp))
    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
    .clip(RoundedCornerShape(16.dp))

fun Modifier.textGradient(colors: List<Color>): Modifier = this.drawWithCache {
    val brush = Brush.linearGradient(colors)
    onDrawWithContent {
        drawContent()
        drawRect(brush, blendMode = BlendMode.SrcAtop)
    }
}
