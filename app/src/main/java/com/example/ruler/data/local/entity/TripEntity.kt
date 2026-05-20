package com.example.ruler.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trips",
    indices = [
        Index(value = ["owner_user_id"]),
        Index(value = ["title"]),
        Index(value = ["destination"])
    ]
)
data class TripEntity(
    @PrimaryKey
    @ColumnInfo(name = "trip_id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "destination")
    val destination: String,
    @ColumnInfo(name = "owner_user_id")
    val ownerUserId: String,
    @ColumnInfo(name = "start_date_epoch_millis")
    val startDateEpochMillis: Long,
    @ColumnInfo(name = "end_date_epoch_millis")
    val endDateEpochMillis: Long,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "budget_amount")
    val budgetAmount: Int,
    @ColumnInfo(name = "budget_currency")
    val budgetCurrency: String,
    @ColumnInfo(name = "cover_emoji")
    val emoji: String,
    @ColumnInfo(name = "hotel_reservation_id")
    val hotelReservationId: String? = null,
    @ColumnInfo(name = "hotel_id")
    val hotelId: String? = null,
    @ColumnInfo(name = "hotel_name")
    val hotelName: String? = null,
    @ColumnInfo(name = "hotel_address")
    val hotelAddress: String? = null,
    @ColumnInfo(name = "hotel_image_url")
    val hotelImageUrl: String? = null,
    @ColumnInfo(name = "hotel_room_id")
    val hotelRoomId: String? = null,
    @ColumnInfo(name = "hotel_room_type")
    val hotelRoomType: String? = null,
    @ColumnInfo(name = "hotel_room_price_per_night")
    val hotelRoomPricePerNight: Double? = null,
    @ColumnInfo(name = "hotel_room_image_urls")
    val hotelRoomImageUrls: String? = null,
    @ColumnInfo(name = "hotel_guest_name")
    val hotelGuestName: String? = null,
    @ColumnInfo(name = "hotel_guest_email")
    val hotelGuestEmail: String? = null,
    @ColumnInfo(name = "hotel_reservation_nights")
    val hotelReservationNights: Int? = null,
    @ColumnInfo(name = "local_hotel_id")
    val localHotelId: String? = null,
    @ColumnInfo(name = "local_hotel_name")
    val localHotelName: String? = null,
    @ColumnInfo(name = "local_hotel_address")
    val localHotelAddress: String? = null,
    @ColumnInfo(name = "local_hotel_check_in_epoch_millis")
    val localHotelCheckInEpochMillis: Long? = null,
    @ColumnInfo(name = "local_hotel_check_out_epoch_millis")
    val localHotelCheckOutEpochMillis: Long? = null
)
