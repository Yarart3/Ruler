package com.example.ruler

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MainActivityStartupTest {

    @Test
    fun mainActivity_startsWithoutCrash() {
        Robolectric.buildActivity(MainActivity::class.java).setup().get()
    }
}
