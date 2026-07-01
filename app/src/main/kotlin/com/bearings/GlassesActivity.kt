package com.bearings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.xr.glimmer.GlimmerTheme
import com.bearings.data.MockCardRepository
import com.bearings.ui.cards.CardStackComposable

class GlassesActivity : ComponentActivity() {

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        ComposeUiFlags.isInitialFocusOnFocusableAvailable = true
        super.onCreate(savedInstanceState)

        setContent {
            GlimmerTheme {
                CardStackComposable(cards = MockCardRepository.CARDS)
            }
        }
    }
}
