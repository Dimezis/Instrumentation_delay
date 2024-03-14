package com.example.instumentationdelay

import androidx.compose.ui.test.junit4.createAndroidComposeRule

import org.junit.Test

import org.junit.Rule

class Test {
    @get:Rule
    val rule = createAndroidComposeRule(StartActivity::class.java)

    @Test
    fun open() {
        // Empty test that just opens the StartActivity with the @Rule
        // Runs for 5s without the workaround, and <1s with the workaround
    }
}