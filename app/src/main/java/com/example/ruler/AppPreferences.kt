package com.example.ruler

import android.content.Context

private const val PREFS_NAME = "ruler_prefs"
private const val ACTIVE_LANGUAGE_KEY = "active_language"
private const val ACTIVE_DARK_MODE_KEY = "active_dark_mode"
private const val USER_LANGUAGE_PREFIX = "user_language_"
private const val USER_DARK_MODE_PREFIX = "user_dark_mode_"

data class ActivePreferences(
    val language: String,
    val darkMode: Boolean
)

object AppPreferences {
    const val DEFAULT_LANGUAGE = "en"
    const val DEFAULT_DARK_MODE = false

    fun getActivePreferences(context: Context): ActivePreferences {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return ActivePreferences(
            language = prefs.getString(ACTIVE_LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE,
            darkMode = prefs.getBoolean(ACTIVE_DARK_MODE_KEY, DEFAULT_DARK_MODE)
        )
    }

    fun activateUserPreferences(context: Context, userId: String): ActivePreferences {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val activePreferences = ActivePreferences(
            language = prefs.getString(userLanguageKey(userId), DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE,
            darkMode = prefs.getBoolean(userDarkModeKey(userId), DEFAULT_DARK_MODE)
        )
        prefs.edit()
            .putString(ACTIVE_LANGUAGE_KEY, activePreferences.language)
            .putBoolean(ACTIVE_DARK_MODE_KEY, activePreferences.darkMode)
            .apply()
        return activePreferences
    }

    fun updateUserPreferences(
        context: Context,
        userId: String,
        language: String,
        darkMode: Boolean
    ) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(userLanguageKey(userId), language)
            .putBoolean(userDarkModeKey(userId), darkMode)
            .putString(ACTIVE_LANGUAGE_KEY, language)
            .putBoolean(ACTIVE_DARK_MODE_KEY, darkMode)
            .apply()
    }

    fun resetActivePreferences(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(ACTIVE_LANGUAGE_KEY, DEFAULT_LANGUAGE)
            .putBoolean(ACTIVE_DARK_MODE_KEY, DEFAULT_DARK_MODE)
            .apply()
    }

    private fun userLanguageKey(userId: String) = "$USER_LANGUAGE_PREFIX$userId"

    private fun userDarkModeKey(userId: String) = "$USER_DARK_MODE_PREFIX$userId"
}
