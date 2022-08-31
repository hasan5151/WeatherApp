package com.huzi.shared.parser

import org.json.JSONArray

object ApiErrorParser {

    fun parseError(error: String?): String? {
        if (error.isNullOrBlank()) return error
        return parseErrorNestedJson(error)
    }

    private fun parseErrorNestedJson(error: String?): String? {
        if (error.isNullOrBlank()) return null
        val startPosition = error.indexOf(':')
        if (startPosition == -1) return null
        val jaRaw = error.subSequence(startPosition + 1, error.length).toString()
        val jaStr = jaRaw.replace("""\r\n""", "").replace("""\""", "")
        return try {
            val ja = JSONArray(jaStr)
            if (ja.length() == 1) {
                val jo = ja.getJSONObject(0)
                jo.getString("message")
            } else {
                error
            }
        } catch (ex: Exception) {
            error
        }
    }
}