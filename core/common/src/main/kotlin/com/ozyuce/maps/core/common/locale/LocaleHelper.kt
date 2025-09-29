package com.ozyuce.maps.core.common.locale

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {
    fun apply(tag: String) {
        val locales = LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(locales)
    }
}

