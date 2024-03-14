package com.example.instumentationdelay

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import org.lsposed.hiddenapibypass.HiddenApiBypass

class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, SecondActivity::class.java))
    }

    override fun onStop() {
        super.onStop()
        // Enable to see the Espresso test finishing in <1s instead of 5s
//        fixInstrumentationDelay()
    }

    private fun fixInstrumentationDelay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.addHiddenApiExemptions("Landroid/app/Activity;")
        }
        try {
            @SuppressLint("BlockedPrivateApi")
            val field = Activity::class.java.getDeclaredField("mEnterAnimationComplete")
            field.isAccessible = true
            field.setBoolean(this, true)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
