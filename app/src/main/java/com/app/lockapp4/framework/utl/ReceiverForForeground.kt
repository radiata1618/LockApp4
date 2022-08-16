package com.app.lockapp4.framework.utl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.File
import androidx.core.content.ContextCompat.startActivity
import com.app.lockapp4.MainActivity
import com.app.lockapp4.getInstantLockDao
import com.app.lockapp4.wakeUpActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime


class ReceiverForForeground : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Timber.d("■■■■■■■■■■■DeleteInstantLockReceiver起動")
        wakeUpActivity(context)//画面を更新して、その起動処理の中で既存のInstantLockデータを削除
    }

}