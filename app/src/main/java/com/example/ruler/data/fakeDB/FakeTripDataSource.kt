package com.example.ruler.data.fakeDB

import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity

object FakeTripDataSource {
    val trips = mutableListOf(
        Trip("1", "Wild Brasil", "São Paulo & Rio, Brazil", "10/07/2025", "24/07/2025", "São Paulo and Rio adventure", "€2,800", "🇧🇷"),
        Trip("2", "Discovering Lleida", "Lleida & Igualada, Spain", "15/03/2025", "17/03/2025", "Local trip around Lleida and Igualada", "€150", "🇪🇸"),
        Trip("3", "Tokyo Adventure", "Tokyo, Japan", "03/08/2025", "15/08/2025", "Exploring Tokyo culture and food", "€2,400", "🇯🇵")
    )

    val activities = mutableListOf<TripActivity>()
}
