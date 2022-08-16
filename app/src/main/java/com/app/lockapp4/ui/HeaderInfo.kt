package com.app.lockapp4.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.lockapp4.framework.database.MainViewModel
import com.app.lockapp4.framework.utl.*
import com.app.lockapp4.presentation.common.CommonVerticalGap16
import java.util.*


@Composable
fun HeaderInfo(
    viewModel:MainViewModel= hiltViewModel()
) {

    var isLockedInstantly=false
    var endTimeLockedInstantly:Calendar?=null

    var isLockedScheduled=false
    var endTimeLockedScheduled:Calendar?=null


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

    val nextOrDuringLockTimeList = viewModel.nextOrDuringLockTime.collectAsState(emptyList()).value

    if(nextOrDuringLockTimeList.isEmpty()){
        isLockedScheduled=false

    }else{

        if(commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].startTimeInLong).after((Calendar.getInstance()))){

            isLockedScheduled=false
        }else{

            isLockedScheduled=true
            endTimeLockedScheduled=commonTranslateLongToCalendar(nextOrDuringLockTimeList[0].endTimeInLong)
        }

    }

    CommonVerticalGap16()
    //スケジュールロック中かどうか、インスタントロック中かどうかの2パターン×2の4パターンで場合分け
    if (isLockedScheduled) {

        LockIcon(true)
        CommonVerticalGap16()
        Text(text = stringIsLocked, fontSize = 36.sp)

        //解除時間の判定
        if(isLockedInstantly){
            //スケジュールロックされているかつ、インスタントロックされている場合の解除時間
            //日時を比べて後の方の時間を表示
            if(endTimeLockedScheduled!!.before(endTimeLockedInstantly)){
                UnlockDateTime(endTimeLockedInstantly!!)
            }else{
                UnlockDateTime(endTimeLockedScheduled)
            }

        }else{
            //スケジュールロックされているかつ、インスタントロックされていない場合の解除時間
            UnlockDateTime(endTimeLockedScheduled!!)
        }
        EmergencyButton()
    } else {

        //スケジュールロックされていないかつ、インスタントロックされている場合の解除時間
        if(isLockedInstantly){

            LockIcon(true)
            Text(text = stringIsLocked, fontSize = 36.sp)
            UnlockDateTime(endTimeLockedInstantly!!)
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

    var showDialog by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("Result") }
    val context = LocalContext.current

    CommonVerticalGap16()
    Button(
        onClick = {
            showDialog = true
        },
        colors = ButtonDefaults.textButtonColors(
            containerColor = Color.Red,
            contentColor = Color.White,
        )
    ) {
        Text(color= Color.White,text = "緊急解除", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                result = "Dismiss"
                showDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.tapEmergencyButton(context)
                        if(viewModel.isEmergencyUnlocked){
                            result = "OK"
                            showDialog = false
                        }
                    },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White,
                            )
                ) {

                    Text(text = "残り"+(constEmergencyTapNumberRequired-viewModel.emergencyTapNumber).toString()+"回")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        result = "Cancel"
                        showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            },
            title = {
                Text("緊急解除")
            },
            text = {
                    Text("ロック解除のために、"+constEmergencyTapNumberRequired.toString()+"回ボタンをタップしてください")
            },
        )
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