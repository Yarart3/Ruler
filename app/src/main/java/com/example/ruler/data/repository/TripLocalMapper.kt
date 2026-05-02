package com.example.ruler.data.repository

import com.example.ruler.data.local.entity.ItineraryItemEntity
import com.example.ruler.data.local.entity.TripEntity
import com.example.ruler.domain.Trip
import com.example.ruler.domain.TripActivity
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val tripDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val tripTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val budgetGroupingFormatter: NumberFormat = NumberFormat.getIntegerInstance(Locale.US).apply {
    isGroupingUsed = true
}

fun TripEntity.toDomain(): Trip {
    return Trip(
        id = id,
        title = title,
        destination = destination,
        startDate = startDateEpochMillis.toLocalDate().format(tripDateFormatter),
        endDate = endDateEpochMillis.toLocalDate().format(tripDateFormatter),
        description = description,
        budget = formatBudget(budgetAmount, budgetCurrency),
        emoji = emoji
    )
}

fun Trip.toEntity(ownerUserId: String): TripEntity {
    val parsedBudget = parseBudget(budget)
    return TripEntity(
        id = id,
        title = title,
        destination = destination,
        ownerUserId = ownerUserId,
        startDateEpochMillis = parseDate(startDate),
        endDateEpochMillis = parseDate(endDate),
        description = description,
        budgetAmount = parsedBudget.amount,
        budgetCurrency = parsedBudget.currency,
        emoji = emoji
    )
}

fun ItineraryItemEntity.toDomain(): TripActivity {
    val dateTime = scheduledAtEpochMillis.toLocalDateTime()
    return TripActivity(
        id = id,
        tripId = tripId,
        title = title,
        description = description,
        date = dateTime.toLocalDate().format(tripDateFormatter),
        time = dateTime.toLocalTime().format(tripTimeFormatter),
        isDone = isDone
    )
}

fun TripActivity.toEntity(displayOrder: Int): ItineraryItemEntity {
    return ItineraryItemEntity(
        id = id,
        tripId = tripId,
        title = title,
        description = description,
        scheduledAtEpochMillis = parseDateTime(date, time),
        durationMinutes = 60,
        displayOrder = displayOrder,
        isDone = isDone
    )
}

fun TripActivity.toEntity(existingItem: ItineraryItemEntity?): ItineraryItemEntity {
    return ItineraryItemEntity(
        id = id,
        tripId = tripId,
        title = title,
        description = description,
        scheduledAtEpochMillis = parseDateTime(date, time),
        durationMinutes = existingItem?.durationMinutes ?: 60,
        displayOrder = existingItem?.displayOrder ?: 0,
        isDone = isDone
    )
}

private fun parseDate(value: String): Long {
    val localDate = LocalDate.parse(value, tripDateFormatter)
    return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

private fun parseDateTime(date: String, time: String): Long {
    val localDate = LocalDate.parse(date, tripDateFormatter)
    val localTime = LocalTime.parse(time, tripTimeFormatter)
    return LocalDateTime.of(localDate, localTime)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}

private fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}

private fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
}

private fun parseBudget(value: String): ParsedBudget {
    val trimmedValue = value.trim()
    val currency = trimmedValue.takeWhile { !it.isDigit() && it != '-' }.trim()
    val numericPart = trimmedValue.filter { it.isDigit() || it == '-' }
    val amount = numericPart.toIntOrNull() ?: 0
    return ParsedBudget(amount = amount, currency = currency)
}

private fun formatBudget(amount: Int, currency: String): String {
    val formattedAmount = budgetGroupingFormatter.format(amount)
    return if (currency.isBlank()) formattedAmount else "$currency$formattedAmount"
}

private data class ParsedBudget(
    val amount: Int,
    val currency: String
)
