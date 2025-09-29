package com.ozyuce.maps.core.common

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * Tarih ve saat i?lemleri i?in yard?mc? s?n?f
 */
object DateTimeUtil {
    
    private const val DATE_FORMAT = "dd.MM.yyyy"
    private const val TIME_FORMAT = "HH:mm"
    private const val DATETIME_FORMAT = "dd.MM.yyyy HH:mm"
    private const val ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    
    private val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale("tr", "TR"))
    private val timeFormatter = SimpleDateFormat(TIME_FORMAT, Locale("tr", "TR"))
    private val dateTimeFormatter = SimpleDateFormat(DATETIME_FORMAT, Locale("tr", "TR"))
    private val isoFormatter = SimpleDateFormat(ISO_FORMAT, Locale("tr", "TR"))
    
    /**
     * Tarih format?n? string olarak d?ner (?rn: 23.09.2025)
     */
    fun formatDate(date: Date): String = dateFormatter.format(date)
    
    /**
     * Saat format?n? string olarak d?ner (?rn: 14:30)
     */
    fun formatTime(date: Date): String = timeFormatter.format(date)
    
    /**
     * Tarih ve saat format?n? string olarak d?ner (?rn: 23.09.2025 14:30)
     */
    fun formatDateTime(date: Date): String = dateTimeFormatter.format(date)
    
    /**
     * ISO format?nda tarih string'i olu?turur (?rn: 2025-09-23T14:30:00)
     */
    fun formatIso(date: Date): String = isoFormatter.format(date)
    
    /**
     * ?u anki tarihi d?ner
     */
    fun now(): Date = Date()
    
    /**
     * ?ki tarih aras?ndaki dakika fark?n? d?ner
     */
    fun differenceInMinutes(start: Date, end: Date): Long {
        return (end.time - start.time) / (1000 * 60)
    }
    
    /**
     * LocalDateTime'? Date'e ?evirir
     */
    fun localDateTimeToDate(localDateTime: LocalDateTime): Date {
        val formatter = DateTimeFormatter.ofPattern(ISO_FORMAT)
        val dateString = localDateTime.format(formatter)
        return isoFormatter.parse(dateString) ?: Date()
    }
}
