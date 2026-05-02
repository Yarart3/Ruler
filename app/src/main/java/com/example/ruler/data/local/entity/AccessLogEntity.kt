package com.example.ruler.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "access_logs",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["occurred_at_epoch_millis"])
    ]
)
data class AccessLogEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "log_id")
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "event_type")
    val eventType: String,
    @ColumnInfo(name = "occurred_at_epoch_millis")
    val occurredAtEpochMillis: Long
)
