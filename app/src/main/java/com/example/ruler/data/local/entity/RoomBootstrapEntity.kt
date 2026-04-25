package com.example.ruler.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_bootstrap")
data class RoomBootstrapEntity(
    @PrimaryKey val id: Int = 1
)
