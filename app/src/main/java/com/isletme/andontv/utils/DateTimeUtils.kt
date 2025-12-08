package com.isletme.andontv.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("tr", "TR"))
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale("tr", "TR"))
    private val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR"))

    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    fun getCurrentTime(): String {
        return timeFormat.format(Date())
    }

    fun getCurrentDateTime(): String {
        return dateTimeFormat.format(Date())
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }
}
