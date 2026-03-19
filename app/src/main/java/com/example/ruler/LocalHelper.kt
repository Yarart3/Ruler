package com.example.ruler

import android.content.Context
import java.util.Locale

object LocaleHelper {

    fun applyLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("ruler_prefs", Context.MODE_PRIVATE)
        return prefs.getString("language", "en") ?: "en"
    }
}