package com.app.lockapp4.framework.utl

import android.icu.text.SimpleDateFormat
import java.util.*


fun commonTranslateTimeIntToString(hour:Int?, minute:Int?):String{

    return if(hour==null||minute==null){
        ""
    }else{
        commonTranslateNumberIntTo2DigitString(hour)+":"+commonTranslateNumberIntTo2DigitString(minute)
    }
}

fun commonTranslateNumberIntTo2DigitString(number:Int):String{
    return if(number>9){number.toString()}else{
        "0$number"
    }
}

fun commonTranslateLongToCalendar(number:Long): Calendar {

    val cal = Calendar.getInstance()
    val date = Date(number)
    cal.time = date
    return cal
}


fun commonTranslateCalendarToStringYYYYMMDDHHMM(cal:Calendar):String{
    return SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(cal.time)
}