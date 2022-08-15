package com.app.lockapp4.framework.utl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.File
import androidx.core.content.ContextCompat.startActivity
import com.app.lockapp4.MainActivity
import com.app.lockapp4.wakeUpActivity
import timber.log.Timber
import java.time.LocalDateTime


class RestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // toast で受け取りを確認
        Timber.d("■■■■■■■■■■■RestartReceiver起動")
        wakeUpActivity(context)
    }

//    private fun startMainActivity(context: Context) {
//        val intent: Intent = Intent(context, MainActivity().javaClass)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(context, intent, null)
//    }
}