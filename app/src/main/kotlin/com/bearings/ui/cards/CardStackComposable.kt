package com.bearings.ui.cards

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Text
import com.bearings.R
import com.bearings.data.CardModel
import kotlin.math.abs

private const val TAG = "BearingsCardStack"

@Composable
fun CardStackComposable(cards: List<CardModel>, modifier: Modifier = Modifier) {
    require(cards.isNotEmpty()) { "CardStack requires at least one card" }

    var currentIndex by remember { mutableIntStateOf(0) }

    fun advance() {
        currentIndex = minOf(currentIndex + 1, cards.size - 1)
        Log.d(TAG, "advance() -> index=$currentIndex")
    }

    fun goBack() {
        currentIndex = maxOf(currentIndex - 1, 0)
        Log.d(TAG, "goBack() -> index=$currentIndex")
    }

    var dragAccumulator by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = 60f

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                Log.d(TAG, "onKeyEvent: ${event.key}")
                when (event.key) {
                    Key.DirectionRight, Key.DirectionDown,
                    Key.DirectionCenter, Key.Enter,
                    Key.NavigateNext, Key.Spacebar -> { advance(); true }
                    Key.DirectionLeft, Key.DirectionUp,
                    Key.NavigatePrevious -> { goBack(); true }
                    else -> false
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    Log.d(TAG, "tap detected")
                    advance()
                })
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { Log.d(TAG, "drag start") },
                    onDragEnd = { dragAccumulator = 0f },
                    onDragCancel = { dragAccumulator = 0f },
                ) { _, dragAmount ->
                    dragAccumulator += dragAmount
                    if (abs(dragAccumulator) > swipeThreshold) {
                        if (dragAccumulator < 0) advance() else goBack()
                        dragAccumulator = 0f
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.90f)
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Crossfade(
                targetState = currentIndex,
                animationSpec = tween(durationMillis = 220),
                label = "card-transition",
            ) { index ->
                CardComposable(
                    card = cards[index],
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(20.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.position_indicator, currentIndex + 1, cards.size))
            }
        }
    }
}
