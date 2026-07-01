package com.bearings.data

class MockCardRepository : CardRepository {
    override suspend fun getCards(): List<CardModel> = CARDS

    companion object {
        val CARDS = listOf(
            CardModel(
                id = "leaving-soon",
                label = "Leaving soon",
                primaryLine = "Leave in 12 min",
                supportingLine = "to reach your 3:00 check-in near La Mariscal, Quito",
                accentSpan = "12 min",
            ),
            CardModel(
                id = "getting-there",
                label = "Getting there",
                primaryLine = "Metro → San Francisco",
                supportingLine = "2 stops · 6 min · platform on your right",
                accentSpan = "San Francisco",
            ),
            CardModel(
                id = "around-you",
                label = "Around you",
                primaryLine = "Café La Ronda",
                supportingLine = "Open until 20:00 · 80 m ahead",
                accentSpan = "80 m",
            ),
            CardModel(
                id = "sign-ahead",
                label = "Sign ahead",
                primaryLine = "‘Salida’ means Exit",
                supportingLine = "follow it to reach street level",
                accentSpan = "‘Salida’",
            ),
            CardModel(
                id = "ask-bearings",
                label = "Ask Bearings",
                primaryLine = "Where’s the nearest pharmacy?",
                supportingLine = "tap and ask, hands-free",
                accentSpan = null,
            ),
        )
    }
}
