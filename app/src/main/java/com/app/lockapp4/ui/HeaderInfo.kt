package com.app.lockapp4.ui

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.lockapp4.framework.database.MainViewModel
import com.app.lockapp4.framework.utl.commonTranslateCalendarToStringYYYYMMDDHHMM
import com.app.lockapp4.framework.utl.commonTranslateLongToCalendar
import com.app.lockapp4.framework.utl.stringIsLocked
import com.app.lockapp4.framework.utl.stringIsUnlocked
import com.app.lockapp4.presentation.common.CommonVerticalGap16
import java.util.*


@Composable
fun HeaderInfo(
    viewModel:MainViewModel= hiltViewModel()
) {

    var isLockedInstantly=false
    var endTimeLockedInstantly:Calendar?=null

    val instantLockList = viewModel.instantLock.collectAsState(emptyList()).value

    if(instantLockList.isEmpty()){

        isLockedInstantly = false

    }else{

        val endTime = commonTranslateLongToCalendar(instantLockList[0].startTimeInLong)
        endTime.add(Calendar.MINUTE,instantLockList[0].durationTimeInLong.toInt())
        if(Calendar.getInstance().after(endTime)){
            isLockedInstantly = false
        }else{

            isLockedInstantly = true
            endTimeLockedInstantly = endTime
        }

    }


    CommonVerticalGap16()
    //スケジュールロック中かどうか、インスタントロック中かどうかの2パターン×2の4パターンで場合分け
    if (viewModel.isLockedByScheduling) {

        LockIcon(true)
        CommonVerticalGap16()
        Text(text = stringIsLocked, fontSize = 36.sp)
        //解除時間の判定
        if(isLockedInstantly){

            if(commonTranslateLongToCalendar(viewModel.unLockedTimeBySchedulingInLong).before(endTimeLockedInstantly)){
                UnlockDateTime(endTimeLockedInstantly!!)
            }else{

                UnlockDateTime(commonTranslateLongToCalendar(viewModel.unLockedTimeBySchedulingInLong))
            }
            Text(text = "解除日時：${viewModel.unLockedTimeBySchedulingInLong}")

        }else{

            Text(text = "解除日時：${viewModel.unLockedTimeBySchedulingInLong}")
        }
        EmergencyButton()
    } else {
        if(isLockedInstantly){

            LockIcon(true)
            Text(text = stringIsLocked, fontSize = 36.sp)
            Text(text = "解除日時：${commonTranslateCalendarToStringYYYYMMDDHHMM(endTimeLockedInstantly!!)}")
            EmergencyButton()

            //ロックしていないパターン
        }else{
            LockIcon(false)
        }
    }

    CommonVerticalGap16()
}

@Composable
fun EmergencyButton(
    viewModel:MainViewModel= hiltViewModel()
){
    CommonVerticalGap16()
    Button(
        onClick = {
            viewModel.deleteInstantLock()
        },
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Red,
            contentColor = Color.White,
        )
    ) {
        Text(color= Color.White,text = "緊急解除", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }

}

@Composable
fun UnlockDateTime(cal: Calendar){

    Text(text = "解除日時：${commonTranslateCalendarToStringYYYYMMDDHHMM(cal)}")
}

@Composable
fun LockIcon(isLocked:Boolean){

    if(isLocked){
        Icon(painter =  rememberVectorPainter(image = Icons.Default.Lock), contentDescription = null,modifier = Modifier.height(40.dp).width(40.dp))
    }else{
        Icon(painter =  rememberVectorPainter(image = Icons.Default.LockOpen), contentDescription = null,modifier = Modifier.height(40.dp).width(40.dp))

    }
}