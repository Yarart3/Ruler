package com.example.ruler.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "itinerary_items",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["trip_id"],
            childColumns = ["trip_owner_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["trip_owner_id"]),
        Index(value = ["scheduled_at_epoch_millis"])
    ]
)
data class ItineraryItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "item_id")
    val id: String,
    @ColumnInfo(name = "trip_owner_id")
    val tripId: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "scheduled_at_epoch_millis")
    val scheduledAtEpochMillis: Long,
    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int,
    @ColumnInfo(name = "display_order")
    val displayOrder: Int,
    @ColumnInfo(name = "is_done")
    val isDone: Boolean = false
)
