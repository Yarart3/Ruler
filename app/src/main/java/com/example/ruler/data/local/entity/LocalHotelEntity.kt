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
    val assignedTripId: String? = null,
    @ColumnInfo(name = "reservation_id")
    val reservationId: String? = null,
    @ColumnInfo(name = "remote_hotel_id")
    val remoteHotelId: String? = null,
    @ColumnInfo(name = "remote_room_id")
    val remoteRoomId: String? = null,
    @ColumnInfo(name = "start_date")
    val startDate: String? = null,
    @ColumnInfo(name = "end_date")
    val endDate: String? = null,
    @ColumnInfo(name = "guest_name")
    val guestName: String? = null,
    @ColumnInfo(name = "guest_email")
    val guestEmail: String? = null,
    @ColumnInfo(name = "hotel_image_url")
    val hotelImageUrl: String? = null,
    @ColumnInfo(name = "room_image_urls")
    val roomImageUrls: String? = null
)
