package com.huzi.shared.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class BooleanString

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class BonusDate

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class HistoryDate

class BonusMoshiAdapter {

    private val zoneId = ZoneId.systemDefault()

    @FromJson
    @BonusDate
    fun fromBonusDate(value: String): Long {
        if (value.isBlank()) return 0
        return try {
            val remoteFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss[.S]")
            val dateTime = LocalDateTime.parse(value, remoteFormatter)
            dateTime.atZone(zoneId).toEpochSecond()
        } catch (ex: Exception) {
            0
        }
    }

    @ToJson
    fun toBonusDate(@BonusDate value: Long): String {
        if (value == 0L) return ""
        return try {
            val instant = Instant.ofEpochSecond(value)
            val dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ISO_INSTANT
            dateTime.format(formatter)
        } catch (ex: Exception) {
            ""
        }
    }

    @FromJson
    @HistoryDate
    fun fromHistoryDate(value: String): Long {
        if (value.isBlank()) return 0
        return try {
            val dateTime = LocalDateTime.parse(value)
            dateTime.atZone(zoneId).toEpochSecond()
        } catch (ex: Exception) {
            0
        }
    }

    @ToJson
    fun toHistoryDate(@HistoryDate value: Long): String {
        if (value == 0L) return ""
        return try {
            val instant = Instant.ofEpochSecond(value)
            val dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ISO_INSTANT
            dateTime.format(formatter)
        } catch (ex: Exception) {
            ""
        }
    }

    @FromJson
    @BooleanString
    fun fromStringBoolean(value: String): Boolean {
        return value == "1"
    }

    @ToJson
    fun toStringBoolean(@BooleanString value: Boolean): String {
        return if (value) "1" else "0"
    }
}