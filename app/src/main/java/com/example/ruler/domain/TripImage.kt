package com.example.ruler.domain

data class TripImage(
    val id: String,
    val tripId: String,
    val uri: String,
    val addedAt: Long
)
