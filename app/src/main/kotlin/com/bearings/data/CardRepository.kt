package com.bearings.data

interface CardRepository {
    suspend fun getCards(): List<CardModel>
}
