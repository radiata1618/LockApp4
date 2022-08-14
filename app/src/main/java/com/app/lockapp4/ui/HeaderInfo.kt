package com.app.lockapp4.ui

import android.icu.text.SimpleDateFormat
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.lockapp4.framework.database.MainViewModel
import com.app.lockapp4.framework.utl.stringIsLocked
import com.app.lockapp4.framework.utl.stringIsUnlocked
import timber.log.Timber
import java.util.*


@Composable
fun HeaderInfo(
    viewModel:MainViewModel= hiltViewModel()
) {

    Timber.d("■■■■■■■■■■■■■■■■■■■■■■■■■■■HeaderInfoはじめ")

    if (viewModel.isScheduledLockedCompanion) {

            Text(text = stringIsLocked, fontSize = 30.sp)
            Text(text = "解除日時：${viewModel.unLockedTimeByScheduling}")
    } else {

        val instantLockList = viewModel.instantLock.collectAsState(emptyList()).value
        if(instantLockList.isEmpty()){
            Text(text = stringIsUnlocked, fontSize = 30.sp)

        }else{

            val calendarTime = Calendar.getInstance()
            calendarTime.add(Calendar.MINUTE,instantLockList[0].durationTimeInLong.toInt())
            Text(text = stringIsLocked, fontSize = 30.sp)
            Text(text = "解除日時：${SimpleDateFormat("yyyy年MM月dd日 HH:mm").format(calendarTime.time)}")
        }
    }

}