package com.example.ruler.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val id: String,
    @ColumnInfo(name = "login_email")
    val email: String,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "birth_date")
    val birthDate: Date?,
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "country")
    val country: String,
    @ColumnInfo(name = "phone")
    val phone: String,
    @ColumnInfo(name = "accepts_marketing_emails")
    val acceptsMarketingEmails: Boolean
)
