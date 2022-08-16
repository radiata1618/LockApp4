package com.app.lockapp4

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.app.lockapp4.framework.database.*
import com.app.lockapp4.framework.utl.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*


fun getDatabase(context: Context): AppDatabase {
    Timber.d("■■■■■■■■■■■getDatabase開始")
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "mainDatabase"
    ).build()
}

fun getLockTimeDao(context: Context): LockTimeDao {
    Timber.d("■■■■■■■■■■■getLockTimeDao開始")
    return getDatabase(context).lockTimeDatabaseDao
}

fun getInstantLockDao(context: Context): InstantLockDao {
    Timber.d("■■■■■■■■■■■getInstantLockDao開始")
    return getDatabase(context).instantLockDatabaseDao
}

fun getNextOrDuringLockTimeDao(context: Context): NextOrDuringLockTimeDao {
    Timber.d("■■■■■■■■■■■getNextOrDuringLockTimeDao開始")
    return getDatabase(context).nextOrDuringLockTimeDatabaseDao
}

fun restartAppOrScheduleRestart(context: Context) {
    Timber.d("■■■■■■■■■■■restartAppOrScheduleRestart開始")

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
                scheduleForBackground(context,commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].startTimeInLong))

            }
        }else{
            //ロック中である
            wakeUpActivity(context)
        }

        Timber.d("■■■■■■■■■■■restartAppOrScheduleRestart終了")
    }
}


fun judgeNowLockedReturnUnlockTime(instantLockDataList: List<InstantLock>, nextOrDuringLockTimeList:List<NextOrDuringLockTime>):Calendar?{//lockされていない場合はNULL

    Timber.d("■■■■■■■■■■■judgeNowLockedReturnUnlockTime開始")
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

    Timber.d("■■■■■■■■■■■judgeNowLockedReturnUnlockTime終了")
}

fun wakeUpActivity(context: Context) {
    Timber.d("■■■■■■■■■■■■■■■■■■■wakeUpActivity開始")

    GlobalScope.launch(Dispatchers.Main) {

        val intent = Intent(context, MainActivity().javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ContextCompat.startActivity(context, intent, null)
        Timber.d("■■■■■■■■■■■■■■■■■■■wakeUpActivity終了")

    }
}

fun insertDefaultLockTimeData(context:Context) {
    Timber.d("■■■■■■■■■■■■■■■■■■■insertDefaultLockTimeData開始")
    GlobalScope.launch(Dispatchers.IO) {
        val lockTimeDao = getLockTimeDao(context)
        val lockData = lockTimeDao.getAll()
        if (lockData.isEmpty()) {
            lockTimeDao.insertAllDefaultData()
        }

        Timber.d("■■■■■■■■■■■■■■■■■■■insertDefaultLockTimeData終了")
    }
}


fun deleteInstantLockData(context:Context) {
    Timber.d("■■■■■■■■■■■■■■■■■■■deleteInstantLockData開始")
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
        Timber.d("■■■■■■■■■■■■■■■■■■■deleteInstantLockData終了")
    }
}

fun cancelRestartPlan(context: Context) {
    Timber.d("■■■■■■■■■■■■■■■■■■■cancelRestartPlan開始")
    // アラームの削除
    val am: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(
        context,
        ReceiverForBackground::class.java
    )
    val pending: PendingIntent = PendingIntent.getBroadcast(
        context, scheduleForDuringBackground, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    pending.cancel()
    am.cancel(pending)
    Timber.d("■■■■■■■■■■■■■■■■■■■cancelRestartPlan終了")
}

fun scheduleForBackground(context: Context, cal:Calendar) {

    Timber.d("■■■■■■■■■■■■■■■■■■■scheduleForBackground開始")
    //明示的なBroadCast
    val intent = Intent(
        context,
        ReceiverForBackground::class.java
    )
    val pending: PendingIntent = PendingIntent.getBroadcast(
        context, scheduleForDuringBackground, intent,
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
    Timber.d("■■■■■■■■■■■■■■■■■■■scheduleForBackground終了")

}

fun cancelScheduleForDuringForeground(context: Context) {
    Timber.d("■■■■■■■■■■■■■■■■■■■cancelScheduleForDuringForeground開始")
    // アラームの削除
    val am: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val intent = Intent(context, ReceiverForForeground::class.java)
    val pending = PendingIntent.getBroadcast(
        context, scheduleForDuringForeground, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    pending.cancel()
    am.cancel(pending)
    Timber.d("■■■■■■■■■■■■■■■■■■■cancelScheduleForDuringForeground終了")
}

fun scheduleForDuringForeground(context: Context) {
    Timber.d("■■■■■■■■■■■■■■■■■■■scheduleForDuringForeground開始")

    val instantLockDataList = getInstantLockDao(context).getAll()
    val nextOrDuringLockTimeList = getNextOrDuringLockTimeDao(context).getAll()

    var unLockTimeCal:Calendar?=judgeNowLockedReturnUnlockTime(instantLockDataList,nextOrDuringLockTimeList)

    if(unLockTimeCal==null&&nextOrDuringLockTimeList.isNotEmpty()){
        //ロックされていない場合、次のスケジュールロックの開始時間を取得
        unLockTimeCal= commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].startTimeInLong)
    }

    if(unLockTimeCal!=null){
        //ロック中、もしくは次のスケジュールがある場合は、その時間にWakeupを呼び出すことで画面情報を更新する
//    明示的なBroadCast
        val intent = Intent(
            context,
            ReceiverForForeground::class.java
        )
        val pending: PendingIntent = PendingIntent.getBroadcast(
            context, scheduleForDuringForeground, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // アラームをセットする
        val am: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//    GlobalScope.launch(Dispatchers.IO) {

//        if (nextLockTime != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                unLockTimeCal.timeInMillis,
                pending
            )

            Timber.d("■■■■■■■■■■■DeleteScheduled：" + commonTranslateCalendarToStringYYYYMMDDHHMM(unLockTimeCal))
//                    am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.timeInMillis+5000, 5000,pending)
        } else {
            Log.d("■■■■■■■■■■■", "routeB")
            am.setExact(AlarmManager.RTC_WAKEUP, unLockTimeCal.timeInMillis, pending)
        }
    }
    Timber.d("■■■■■■■■■■■■■■■■■■■scheduleForDuringForeground終了")
}


fun recalculateNextOrDuringLockTime(context: Context) {

    Timber.d("■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime開始")

    val instantLockDao = getInstantLockDao(context)
    val lockTimeDao = getLockTimeDao(context)
    val nextOrDuringLockTimeDao = getNextOrDuringLockTimeDao(context)

    val newNextOrDuringScheduleLockTime = lockTimeDao.getNextOrDuringScheduleLockTime()
    if(newNextOrDuringScheduleLockTime!=null){
        Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■newNextOrDuringScheduleLockTime:スタートタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
            commonTranslateLongToCalendar(newNextOrDuringScheduleLockTime.startTimeInLong)))
        Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■newNextOrDuringScheduleLockTime:エンドタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
            commonTranslateLongToCalendar(newNextOrDuringScheduleLockTime.endTimeInLong)))
    }
    val nextOrDuringLockTimeList = nextOrDuringLockTimeDao.getAll()

    if(nextOrDuringLockTimeList.isNotEmpty()){

        Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■oldNextOrDuringScheduleLockTime:スタートタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
            commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].startTimeInLong)))
        Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■oldNextOrDuringScheduleLockTime:エンドタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
            commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].endTimeInLong)))
    }

//        GlobalScope.launch(Dispatchers.Main) {
    Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime1")

    if (newNextOrDuringScheduleLockTime != null) {
        //今最新状況でデータ投入すべきはず

        Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime2")
        if (nextOrDuringLockTimeList.isEmpty()) {
            //今最新状況でロックの最中かつ、もともとデータなかった
            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime3")
            nextOrDuringLockTimeDao.insert(newNextOrDuringScheduleLockTime)

        } else {
            //今最新状況でロックの最中かつ、もともとロック中だった
//
//            Timber.d("★★★★★★★★★★★★★ジャッジ情報★★★★★★★★★★★★★")
//
//            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■oldNextOrDuringScheduleLockTime:エンドタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
//                commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].endTimeInLong)))
//            Timber.d(nextOrDuringLockTimeList[0].endTimeInLong.toString())
//            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■newNextOrDuringScheduleLockTime:エンドタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
//                commonTranslateLongToCalendar(newNextOrDuringScheduleLockTime.endTimeInLong)))
//            Timber.d(newNextOrDuringScheduleLockTime.endTimeInLong.toString())
//
//            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■oldNextOrDuringScheduleLockTime:スタートタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
//                commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].startTimeInLong)))
//            Timber.d(nextOrDuringLockTimeList[0].startTimeInLong.toString())
//            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■newNextOrDuringScheduleLockTime:スタートタイム："+ commonTranslateCalendarToStringYYYYMMDDHHMM(
//                commonTranslateLongToCalendar(newNextOrDuringScheduleLockTime.startTimeInLong)))
//            Timber.d(newNextOrDuringScheduleLockTime.startTimeInLong.toString())

            if(nextOrDuringLockTimeList[0].endTimeInLong==newNextOrDuringScheduleLockTime.endTimeInLong
                &&nextOrDuringLockTimeList[0].startTimeInLong==newNextOrDuringScheduleLockTime.startTimeInLong){
                Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime4")
                //データが変わっていないので処理無し
            }else{

                Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime5")
                nextOrDuringLockTimeDao.update(newNextOrDuringScheduleLockTime)
            }

        }

    } else {
        //今最新状況でロックの最中ではない
        if (nextOrDuringLockTimeList.isEmpty()) {
            //今最新状況でロックの最中ではない、もともとロック中じゃなかった
            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime5")

            //処理無し

        } else {
            //今最新状況でロックの最中ではない、もともとロック中だった
            Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■nextOrDuringLockTimeDao.deleteAll()実行します")
            nextOrDuringLockTimeDao.deleteAll()

        }
    }

    Timber.d("■■■■■■■■■■■■■■■■■■■recalculateNextOrDuringLockTime終了")
}