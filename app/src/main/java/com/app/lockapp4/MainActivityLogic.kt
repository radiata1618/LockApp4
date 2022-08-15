package com.app.lockapp4

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.app.lockapp4.framework.database.AppDatabase
import com.app.lockapp4.framework.database.InstantLockDao
import com.app.lockapp4.framework.database.LockTimeDao
import com.app.lockapp4.framework.utl.AlarmBroadcastReceiver
import com.app.lockapp4.framework.utl.commonTranslateLongToCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

fun getDatabase(context: Context) :AppDatabase{
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "mainDatabase"
    ).build()
}

fun getLockTimeDao(context: Context): LockTimeDao {
    return getDatabase(context).lockTimeDatabaseDao
}

fun getInstantLockDao(context: Context): InstantLockDao {
    return getDatabase(context).instantLockDatabaseDao
}

fun restartApp(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val instantLockDataList = getInstantLockDao(context).getAll()

        if(instantLockDataList.isEmpty()){
            ifScheduledWakeUp(context)

        }else{
            val now = Calendar.getInstance()

            val endTime = commonTranslateLongToCalendar(instantLockDataList[0].startTimeInLong)
            endTime.add(Calendar.MINUTE,instantLockDataList[0].durationTimeInLong.toInt())

            if(now.before(endTime)){
                wakeUpActivity(context)
            }else{
                getInstantLockDao(context).deleteAll()
                ifScheduledWakeUp(context)
            }
        }

    }
}

fun ifScheduledWakeUp(context: Context){

    val nextTime = getLockTimeDao(context).getNextToTimeIfScheduledLocking()

    if(nextTime!=null){
        Timber.d("■■■■■■■■■■■■■■■■■■■ifScheduledWakeUpの中のwakeUpActivity(context)の前")
        wakeUpActivity(context)
    }
}

fun wakeUpActivity(context: Context){
    Timber.d("■■■■■■■■■■■■■■■■■■■wakeUpActivity始まり")

    GlobalScope.launch(Dispatchers.Main) {

        val intent = Intent(context, MainActivity().javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(context, intent, null)

    }
}

fun setRestartPlan(context: Context) {

    Log.d("■■■■■■■■■■■", "setRestartPlanが呼び出される")
    //明示的なBroadCast
    val intent = Intent(
        context,
        AlarmBroadcastReceiver::class.java
    )
    val pending: PendingIntent = PendingIntent.getBroadcast(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // アラームをセットする
    val am: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    GlobalScope.launch(Dispatchers.IO) {
        val nextLockTime = getLockTimeDao(context).getNextFromTime()
        if (nextLockTime != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("■■■■■■■■■■■", "★routeA★")
                am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextLockTime.timeInMillis,
                    pending
                )
//                    am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis+5000, 5000,pending)
            } else {
                Log.d("■■■■■■■■■■■", "routeB")
                am.setExact(AlarmManager.RTC_WAKEUP, nextLockTime.timeInMillis, pending)
//                    am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis, 5000,pending)
            }

        }
    }
}


fun deleteInstantLockSchedule(context: Context) {

//    明示的なBroadCast
    val intent = Intent(
        context,
        AlarmBroadcastReceiver::class.java
    )
    val pending: PendingIntent = PendingIntent.getBroadcast(
        context, 1, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // アラームをセットする
    val am: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    GlobalScope.launch(Dispatchers.IO) {
        val nextLockTime = getLockTimeDao(context).getNextFromTime()
        if (nextLockTime != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d("■■■■■■■■■■■", "★routeA★")
                am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextLockTime.timeInMillis,
                    pending
                )
//                    am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis+5000, 5000,pending)
            } else {
                Log.d("■■■■■■■■■■■", "routeB")
                am.setExact(AlarmManager.RTC_WAKEUP, nextLockTime.timeInMillis, pending)
            }

        }
    }
}