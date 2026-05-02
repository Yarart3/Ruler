package com.example.ruler.data.repository

import com.example.ruler.data.local.entity.UserEntity
import com.example.ruler.domain.UserProfile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val userBirthDateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
    isLenient = false
}

fun UserEntity.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        email = email,
        username = username,
        birthDate = birthDate?.let(userBirthDateFormatter::format).orEmpty(),
        address = address,
        country = country,
        phone = phone,
        acceptsMarketingEmails = acceptsMarketingEmails
    )
}

fun UserProfile.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        email = email,
        username = username,
        birthDate = birthDate.toBirthDateOrNull(),
        address = address,
        country = country,
        phone = phone,
        acceptsMarketingEmails = acceptsMarketingEmails
    )
}

private fun String.toBirthDateOrNull(): Date? {
    if (isBlank()) {
        return null
    }
    return runCatching { userBirthDateFormatter.parse(this) }.getOrNull()
}
