package com.example.ruler.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "local_hotels",
    indices = [Index(value = ["owner_user_id"])]
)
data class LocalHotelEntity(
    @PrimaryKey
    @ColumnInfo(name = "hotel_id")
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "owner_user_id")
    val ownerUserId: String,
    @ColumnInfo(name = "nights")
    val nights: Int = 0,
    @ColumnInfo(name = "price_per_night")
    val pricePerNight: Double = 0.0,
    @ColumnInfo(name = "assigned_trip_id")
    val assignedTripId: String? = null
)
