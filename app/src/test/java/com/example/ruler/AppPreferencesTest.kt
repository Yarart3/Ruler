package com.example.ruler

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppPreferencesTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("ruler_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun defaults_areLightAndEnglish() {
        val active = AppPreferences.getActivePreferences(context)

        assertEquals("en", active.language)
        assertFalse(active.darkMode)
    }

    @Test
    fun activateUserPreferences_loadsPerUserValues() {
        AppPreferences.updateUserPreferences(context, "user-1", "ca", true)
        AppPreferences.updateUserPreferences(context, "user-2", "es", false)

        val user1 = AppPreferences.activateUserPreferences(context, "user-1")
        val user2 = AppPreferences.activateUserPreferences(context, "user-2")

        assertEquals("ca", user1.language)
        assertTrue(user1.darkMode)
        assertEquals("es", user2.language)
        assertFalse(user2.darkMode)
    }

    @Test
    fun resetActivePreferences_restoresDefaultsWithoutDeletingUserSettings() {
        AppPreferences.updateUserPreferences(context, "user-1", "ca", true)

        AppPreferences.resetActivePreferences(context)
        val reset = AppPreferences.getActivePreferences(context)
        val restored = AppPreferences.activateUserPreferences(context, "user-1")

        assertEquals("en", reset.language)
        assertFalse(reset.darkMode)
        assertEquals("ca", restored.language)
        assertTrue(restored.darkMode)
    }
}
