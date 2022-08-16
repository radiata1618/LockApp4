package com.app.lockapp4

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.room.Room
import com.app.lockapp4.framework.database.*
import com.app.lockapp4.framework.utl.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*


fun getDatabase(context: Context): AppDatabase {
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

fun getNextOrDuringLockTimeDao(context: Context): NextOrDuringLockTimeDao {
    return getDatabase(context).nextOrDuringLockTimeDatabaseDao
}

fun restartApp(context: Context) {

    GlobalScope.launch(Dispatchers.IO) {
        val instantLockDataList = getInstantLockDao(context).getAll()
        val nextOrDuringLockTimeList = getNextOrDuringLockTimeDao(context).getAll()

        val unLockTimeCal:Calendar?=judgeNowLockedReturnUnlockTime(instantLockDataList,nextOrDuringLockTimeList)

        if(unLockTimeCal==null){
            //ロック中でない
            //スケジュールされた未来のロック時間があるかどうか
            if(nextOrDuringLockTimeList.isEmpty()){
                //ロック中でなく、スケジュールされたロックもなし
            }else{
                setRestartPlan(context,commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].endTimeInLong))

            }
        }else{
            //ロック中である
            wakeUpActivity(context)
        }

    }
}


fun judgeNowLockedReturnUnlockTime(instantLockDataList: List<InstantLock>, nextOrDuringLockTimeList:List<NextOrDuringLockTime>):Calendar?{//lockされていない場合はNULL

    if (instantLockDataList.isEmpty()) {
        //インスタントロックされていない
        if(nextOrDuringLockTimeList.isNotEmpty()){
            if(commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].startTimeInLong).before(Calendar.getInstance())){
                //インスタントロックされていないかつスケジュールロックされている
                return commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].endTimeInLong)
            }else{
                //インスタントロックされていないかつスケジュールロックされていない
                return null
            }
        }else{
            //インスタントロックされていないかつスケジュールロックされていない
            return null
        }

    } else {
        //インスタントロックされている
        var calInstantLockEndTime=Calendar.getInstance()
        calInstantLockEndTime.timeInMillis = instantLockDataList[0].startTimeInLong
        calInstantLockEndTime.add(Calendar.MINUTE,instantLockDataList[0].durationTimeInLong.toInt())

        if(nextOrDuringLockTimeList.isNotEmpty()){
            if(commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].startTimeInLong).before(Calendar.getInstance())){
                //インスタントロックされているかつスケジュールロックされている
                if(calInstantLockEndTime.after(commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].endTimeInLong))){
                    return calInstantLockEndTime
                }else{
                    return commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].endTimeInLong)
                }
            }else{
                //インスタントロックされているかつスケジュールロックされていない
                return calInstantLockEndTime
            }
        }else{
            //インスタントロックされているかつスケジュールロックされていない
            return calInstantLockEndTime
        }
    }
}

fun wakeUpActivity(context: Context) {
    Timber.d("■■■■■■■■■■■■■■■■■■■wakeUpActivity始まり")

    GlobalScope.launch(Dispatchers.Main) {

        val intent = Intent(context, MainActivity().javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(context, intent, null)

    }
}

fun insertDefaultLockTimeData(context:Context) {
    GlobalScope.launch(Dispatchers.IO) {
        val lockTimeDao = getLockTimeDao(context)
        val lockData = lockTimeDao.getAll()
        if (lockData.isEmpty()) {
            lockTimeDao.insertAllDefaultData()
        }

    }
}


fun deleteInstantLockData(context:Context) {
    GlobalScope.launch(Dispatchers.IO) {
        val instantLockDao = getInstantLockDao(context)
        var instantLockList =instantLockDao.getAll()
        if(instantLockList.isNotEmpty()){

            val now = Calendar.getInstance()
            val endTime = commonTranslateLongToCalendar(instantLockList[0].startTimeInLong)
            endTime.add(Calendar.MINUTE,instantLockList[0].durationTimeInLong.toInt())

            if(now!!.after(endTime!!)){
                instantLockDao.deleteAll()
            }
        }
    }
}

fun cancelRestartPlan(context: Context) {
    // アラームの削除
    val am: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(
        context,
        RestartReceiver::class.java
    )
    val pending: PendingIntent = PendingIntent.getBroadcast(
        context, alarmManagerRequestCodeRestartScheduler, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    pending.cancel()
    am.cancel(pending)
}

fun setRestartPlan(context: Context,cal:Calendar) {

    Log.d("■■■■■■■■■■■", "setRestartPlanが呼び出される")
    //明示的なBroadCast
    val intent = Intent(
        context,
        RestartReceiver::class.java
    )
    val pending: PendingIntent = PendingIntent.getBroadcast(
        context, alarmManagerRequestCodeRestartScheduler, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // アラームをセットする
    val am: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//    GlobalScope.launch(Dispatchers.IO) {
        val nextLockTime = cal
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

//        }
    }
}

fun cancelDeleteInstantLockSchedule(context: Context) {
    // アラームの削除
    val am: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, DeleteInstantLockReceiver::class.java)
    val pending = PendingIntent.getBroadcast(
        context, alarmManagerRequestCodeDeleteScheduler, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    pending.cancel()
    am.cancel(pending)
}

fun restartForDeleteInstantLockSchedule(context: Context, endTime: Calendar) {

    Timber.d("■■■■■■■■■■■■■■■■deleteInstantLockSchedule開始")
//    明示的なBroadCast
    val intent = Intent(
        context,
        DeleteInstantLockReceiver::class.java
    )
    val pending: PendingIntent = PendingIntent.getBroadcast(
        context, alarmManagerRequestCodeDeleteScheduler, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // アラームをセットする
    val am: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//    GlobalScope.launch(Dispatchers.IO) {

//        if (nextLockTime != null) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        am.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            endTime.timeInMillis,
            pending
        )

        Timber.d("■■■■■■■■■■■DeleteScheduled：" + commonTranslateCalendarToStringYYYYMMDDHHMM(endTime))
//                    am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis+5000, 5000,pending)
    } else {
        Log.d("■■■■■■■■■■■", "routeB")
        am.setExact(AlarmManager.RTC_WAKEUP, endTime.timeInMillis, pending)
    }

//        }

    Timber.d("■■■■■■■■■■■■■■■■deleteInstantLockSchedule終了")
//    }
}