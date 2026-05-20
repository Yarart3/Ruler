package com.example.ruler.domain

data class LocalHotel(
    val id: String,
    val name: String,
    val address: String,
    val nights: Int = 0,
    val pricePerNight: Double = 0.0,
    val assignedTripId: String? = null
)
