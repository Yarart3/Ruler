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
    val emoji: String
)
