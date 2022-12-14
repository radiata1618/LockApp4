package com.app.lockapp4.framework.database

import android.util.Log
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface LockTimeDao {
    @Insert
    fun insert(lockTime: LockTime): Long

    @Update
    fun update(lockTime: LockTime)

    @Query("delete from lockTime where dayId = :id")
    fun delete(id: Int)

    @Query("delete from lockTime")
    fun deleteAll()

    @Query("select * from lockTime")
    fun getAll(): List<LockTime>

    @Query("SELECT * FROM lockTime ORDER BY dayId ASC")
    fun getAllFlow(): Flow<List<LockTime>>

    @Query("select * from lockTime where dayId = :id")
    fun getLockTime(id: Int): LockTime

    @Query("select * from lockTime where dayId = :id")
    fun getLockTimeFlow(id: Int): Flow<LockTime>


    fun getNextOrDuringScheduleLockTime(): NextOrDuringLockTime? {

        val cal = Calendar.getInstance()

        //昨日の曜日をまずはチェック
        var dayOfWeekToday = cal.get(Calendar.DAY_OF_WEEK)
        var dayOfWeekYesterday = yesterdayDayOfWeekNumberFrom1To7(dayOfWeekToday)

        var lockTimeData = getLockTime(dayOfWeekYesterday)

        if (lockTimeData == null) {
            return null
        }

        //前日チェック
        if (lockTimeData.enableLock) {//有効なスケジュール
            if (isAfterByHHMM(
                    lockTimeData.toTimeHour,
                    lockTimeData.toTimeMinute,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE)
                )//TOが未来日時
            ) {

                cal.set(Calendar.HOUR_OF_DAY, lockTimeData.toTimeHour)
                cal.set(Calendar.MINUTE, lockTimeData.toTimeMinute)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)

                return NextOrDuringLockTime(
                    0,
                    beforeCal(
                        cal,
                        lockTimeData.fromTimeHour,
                        lockTimeData.fromTimeMinute
                    ).timeInMillis,
                    cal.timeInMillis
                )

            }
        }

        var dayOfWeek = dayOfWeekYesterday

        //先1週間を順にチェック
        for (i in 1..7) {
            dayOfWeek = tommorowDayOfWeekNumberFrom1To7(dayOfWeek)
            cal.add(Calendar.DATE, 1)
            lockTimeData = getLockTime(dayOfWeek)

            if (lockTimeData.enableLock) {
                cal.set(Calendar.HOUR_OF_DAY, lockTimeData.toTimeHour)
                cal.set(Calendar.MINUTE, lockTimeData.toTimeMinute)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)

                return NextOrDuringLockTime(
                    0,
                    beforeCal(
                        cal,
                        lockTimeData.fromTimeHour,
                        lockTimeData.fromTimeMinute
                    ).timeInMillis,
                    cal.timeInMillis
                )
            }
        }
        return null
    }

    fun beforeCal(baseCal: Calendar, hour: Int, minute: Int): Calendar {
        var cal = Calendar.getInstance()
        cal.set(Calendar.DATE, baseCal.get(Calendar.DATE))
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        if (cal.after(baseCal)) {
            cal.add(Calendar.DATE, -1)
        }

        return cal
    }

    fun tommorowDayOfWeekNumberFrom1To7(todayNumber: Int): Int {

        var dayOfWeekYesterday = todayNumber + 1
        if (dayOfWeekYesterday == 8) {
            dayOfWeekYesterday = 1
        }
        return dayOfWeekYesterday
    }

    fun yesterdayDayOfWeekNumberFrom1To7(todayNumber: Int): Int {

        var dayOfWeekYesterday = todayNumber - 1
        if (dayOfWeekYesterday == 0) {
            dayOfWeekYesterday = 7
        }
        return dayOfWeekYesterday
    }

    fun isAfterByHHMM(mainHH: Int, mainMM: Int, subHH: Int, subMM: Int): Boolean {
        if (mainHH > subHH) {
            return true
        } else if (mainHH == subHH && mainMM > subMM) {
            return true
        }
        return false
    }
//
//    fun getNextToTimeIfScheduledLocking(): Calendar? {
//
//        val cal = Calendar.getInstance()
//
//        //昨日の曜日をまずはチェック
//
//        var dayOfWeekToday = cal.get(Calendar.DAY_OF_WEEK)
//        var dayOfWeekYesterday = cal.get(Calendar.DAY_OF_WEEK) - 1
//        if (dayOfWeekYesterday == 0) {
//            dayOfWeekYesterday = 7
//        }
//
//        var lockTimeDataYesterday = getLockTime(dayOfWeekYesterday)
//
//        if(lockTimeDataYesterday==null){
//            return null
//        }
//
//        var tmpBoolean=false
//        //前日チェック
//        if (lockTimeDataYesterday.enableLock) {//有効なスケジュール
//
//            tmpBoolean =
//                        lockTimeDataYesterday.fromTimeHour > 12
//                        || lockTimeDataYesterday.fromTimeHour < cal.get(Calendar.HOUR_OF_DAY)
//                        ||( (lockTimeDataYesterday.fromTimeHour == cal.get(Calendar.HOUR_OF_DAY)) && (lockTimeDataYesterday.fromTimeMinute < cal.get(Calendar.MINUTE)))
//
//            if (tmpBoolean) {//Fromを過ぎているかどうか
//
//                tmpBoolean = cal.get(Calendar.HOUR_OF_DAY) < lockTimeDataYesterday.toTimeHour
//                            ||( (cal.get(Calendar.HOUR_OF_DAY) == lockTimeDataYesterday.toTimeHour ) && (cal.get(Calendar.MINUTE) < lockTimeDataYesterday.toTimeMinute))
//
//                Log.d("■■■■■■■■■","今の時"+cal.get(Calendar.HOUR_OF_DAY).toString())
//                Log.d("■■■■■■■■■","lockTimeDataYesterday.toTimeHour:"+lockTimeDataYesterday.toTimeHour)
//                Log.d("■■■■■■■■■",tmpBoolean.toString()+"前日チェックToの値")
//                if (tmpBoolean) {//TO以前かどうか
//
//                    Log.d("■■■■■■■■■","前日チェックTOの前")
//                    cal.set(Calendar.HOUR_OF_DAY,lockTimeDataYesterday.toTimeHour)
//                    cal.set(Calendar.MINUTE,lockTimeDataYesterday.toTimeMinute)
//                    return cal
//
//                }
//            }
//        }
//
//        var lockTimeDataToday = getLockTime(dayOfWeekToday)
//        //当日チェック
//        if (lockTimeDataToday.enableLock) {//有効なスケジュール
//
//            val tmpBoolean =
//                        12 <= lockTimeDataToday.fromTimeHour
//                        && (lockTimeDataToday.fromTimeHour < cal.get(Calendar.HOUR_OF_DAY)
//                        ||( (lockTimeDataToday.fromTimeHour == cal.get(Calendar.HOUR_OF_DAY)) && (lockTimeDataToday.fromTimeMinute < cal.get(Calendar.MINUTE))))
//
//            if (tmpBoolean) {//Fromを過ぎているかどうか
//
//                Log.d("■■■■■■■■■","当日チェックFromを過ぎている")
//                cal.set(Calendar.HOUR_OF_DAY,lockTimeDataToday.toTimeHour)
//                cal.set(Calendar.MINUTE,lockTimeDataToday.toTimeMinute)
//                cal.add(Calendar.DATE,1)
//                return cal
//            }
//        }
//
//        return null
//    }
//
//    fun getNextFromTime(): Calendar? {
//        val now = Calendar.getInstance()
//
//        //昨日の曜日をまずはチェック
//        var dayOfWeek = now.get(Calendar.DAY_OF_WEEK) - 1
//        if (dayOfWeek == 0) {
//            dayOfWeek = 7
//        }
//
//        Log.d("■■■■■■■■■■■■WeekId", dayOfWeek.toString())
//        var lockTimeData = getLockTime(dayOfWeek)
//
//        if (lockTimeData.enableLock && lockTimeData.fromTimeHour < 12) {
//            val calendarTmp =
//                getCalendarTodayByHourMinute(lockTimeData.fromTimeHour, lockTimeData.fromTimeMinute)
//            if (Calendar.getInstance() < getCalendarTodayByHourMinute(
//                    lockTimeData.fromTimeHour,
//                    lockTimeData.fromTimeMinute
//                )
//            ) {
//                return calendarTmp
//            }
//        }
//
//        //先1週間を順にチェック
//        for (i in 1..7) {
//            dayOfWeek += 1
//            if (dayOfWeek > 7) {
//                dayOfWeek -= 7
//            }
//            Log.d("■■■■■■■■■■■■", dayOfWeek.toString()+"曜日")
//            lockTimeData = getLockTime(dayOfWeek)
//            if (lockTimeData.enableLock) {
//                val calendarTmp = getCalendarTodayByHourMinute(
//                    lockTimeData.fromTimeHour,
//                    lockTimeData.fromTimeMinute
//                )
//                calendarTmp.add(Calendar.DATE, i - 1)
//                //翌日かどうかで場合分け
//                if (lockTimeData.fromTimeHour < 12) {
//                    calendarTmp.add(Calendar.DATE, 1)
//                }
//                //過ぎた日付じゃなければ終わり
//                if(now.before(calendarTmp)){
//                    return calendarTmp
//                }
//            }
//        }
//        return null
//    }


    fun insertAllDefaultData() {
        insertDefaultDataUnit(1, "日")
        insertDefaultDataUnit(2, "月")
        insertDefaultDataUnit(3, "火")
        insertDefaultDataUnit(4, "水")
        insertDefaultDataUnit(5, "木")
        insertDefaultDataUnit(6, "金")
        insertDefaultDataUnit(7, "土")
    }

    fun insertDefaultDataUnit(dayId: Int, dayName: String) {
        insert(
            LockTime(
                dayId,
                dayName,
                23,
                0,
                false,
                6,
                0,
                false
            )
        )
    }

    fun updateEnable(lockTime: LockTime) {
        Log.d("■■■■■■■■■■■■■■", "updateEnable")
        update(
            LockTime(
                lockTime.dayId,
                lockTime.dayName,
                lockTime.fromTimeHour,
                lockTime.fromTimeMinute,
                lockTime.fromBeforeDay,
                lockTime.toTimeHour,
                lockTime.toTimeMinute,
                !lockTime.enableLock,
            )
        )

    }

    fun updateFromTime(dayId: Int, fromTimeHour: Int, fromTimeMinute: Int) {

        var lockTime = getLockTime(dayId)

        update(
            LockTime(
                lockTime.dayId,
                lockTime.dayName,
                fromTimeHour,
                fromTimeMinute,
                calcFromBeforeDay(
                    fromTimeHour,
                    fromTimeMinute,
                    lockTime.toTimeHour,
                    lockTime.toTimeMinute
                ),
                lockTime.toTimeHour,
                lockTime.toTimeMinute,
                lockTime.enableLock,
            )
        )
    }

    fun updateToTime(dayId: Int, toTimeHour: Int, toTimeMinute: Int) {

        var lockTime = getLockTime(dayId)

        update(
            LockTime(
                lockTime.dayId,
                lockTime.dayName,
                lockTime.fromTimeHour,
                lockTime.fromTimeMinute,
                calcFromBeforeDay(
                    lockTime.fromTimeHour,
                    lockTime.fromTimeMinute,
                    toTimeHour,
                    toTimeMinute
                ),
                toTimeHour,
                toTimeMinute,
                lockTime.enableLock,
            )
        )
    }

    fun calcFromBeforeDay(
        fromTimeHour: Int,
        fromTimeMinute: Int,
        toTimeHour: Int,
        toTimeMinute: Int
    ): Boolean {

        return if (fromTimeHour < toTimeHour) {
            true;
        } else if (fromTimeHour == toTimeHour) {

            if (fromTimeMinute < toTimeMinute) {
                true;
            } else if (fromTimeMinute == toTimeMinute) {
                false;

            } else {
                false;
            }
        } else {
            false;
        }

    }

}