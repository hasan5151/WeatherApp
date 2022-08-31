package com.huzi.shared.extensions

import java.text.SimpleDateFormat
import java.util.*

fun String.toDate() : String{
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ru"))
    val date = inputFormat.parse(this)
    val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru"))
    return  outputFormat.format(date)
}