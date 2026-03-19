package com.example.ruler

import android.app.Application
import android.content.Context

class RulerApp : Application() {
    override fun attachBaseContext(base: Context) {
        val lang = LocaleHelper.getSavedLanguage(base)
        super.attachBaseContext(LocaleHelper.applyLocale(base, lang))
    }
}