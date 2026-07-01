package com.bearings.data

data class CardModel(
    val id: String,
    val label: String,
    val primaryLine: String,
    val supportingLine: String,
    val accentSpan: String? = null,
)
