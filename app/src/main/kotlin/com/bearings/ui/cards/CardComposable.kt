package com.bearings.ui.cards

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import com.bearings.data.CardModel

@Composable
fun CardComposable(card: CardModel, modifier: Modifier = Modifier) {
    val primaryAnnotated = buildAnnotatedString {
        val span = card.accentSpan
        if (span != null && card.primaryLine.contains(span)) {
            val idx = card.primaryLine.indexOf(span)
            append(card.primaryLine.substring(0, idx))
            withStyle(
                SpanStyle(color = GlimmerTheme.colors.primary, fontWeight = FontWeight.Bold)
            ) { append(span) }
            append(card.primaryLine.substring(idx + span.length))
        } else {
            append(card.primaryLine)
        }
    }

    Card(
        modifier = modifier,
        header = { Text(card.label.uppercase(), style = GlimmerTheme.typography.caption) },
        leadingIcon = {
            Icon(
                imageVector = iconFor(card.id),
                contentDescription = null,
                tint = GlimmerTheme.colors.primary,
            )
        },
        title = { Text(primaryAnnotated) },
        subtitle = { Text(card.supportingLine) },
    ) {}
}

private fun iconFor(cardId: String): ImageVector = when (cardId) {
    "leaving-soon" -> Icons.Filled.Schedule
    "getting-there" -> Icons.Filled.Train
    "around-you" -> Icons.Filled.LocalCafe
    "sign-ahead" -> Icons.Filled.Translate
    "ask-bearings" -> Icons.Filled.Mic
    else -> Icons.Outlined.Info
}
