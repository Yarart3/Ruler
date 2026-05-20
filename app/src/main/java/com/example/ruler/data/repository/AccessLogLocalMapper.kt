package com.example.ruler.data.repository

import com.example.ruler.data.local.entity.AccessLogEntity
import com.example.ruler.domain.AccessLogEntry

const val ACCESS_LOG_EVENT_LOGIN = "LOGIN"
const val ACCESS_LOG_EVENT_LOGOUT = "LOGOUT"

fun AccessLogEntity.toDomain(): AccessLogEntry = AccessLogEntry(
    id = id,
    userId = userId,
    eventType = eventType,
    occurredAtEpochMillis = occurredAtEpochMillis
)
