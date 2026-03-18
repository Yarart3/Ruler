package com.example.ruler.domain

data class Trip(
    val id: String,
    val title: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val description: String,
    val budget: String,
    val emoji: String
)
