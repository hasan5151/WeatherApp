package com.huzi.shared.moshi

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class MoshiDate

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class MoshiDateTime

@Suppress("unused")
class DateAdapter {

    @FromJson
    @MoshiDate
    fun fromDateStr(value: String): LocalDate {
        if (value.isBlank()) return LocalDate.MIN
        return try {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            LocalDate.parse(value, dateFormatter)
        } catch (ex: Exception) {
            LocalDate.MIN
        }
    }

    @ToJson
    fun toDateStr(@MoshiDate value: LocalDate): String {
        return value.toString()
    }

    @FromJson
    @MoshiDateTime
    fun fromDateTimeStr(value: String): LocalDateTime {
        if (value.isBlank()) return LocalDateTime.MIN
        return try {
            LocalDateTime.parse(value)
        } catch (ex: Exception) {
            LocalDateTime.MIN
        }
    }

    @ToJson
    fun toDateTimeStr(@MoshiDateTime value: LocalDateTime): String {
        return value.toString()
    }
}