package com.app.lockapp4.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.lockapp4.framework.database.MainViewModel
import com.app.lockapp4.presentation.common.CommonVerticalGap6


@Composable
fun LinesDayOfWeek(
    viewModel: MainViewModel = hiltViewModel()
) {

    var lockTimeList = viewModel.lockTimes.collectAsState(emptyList()).value
    Box(modifier = Modifier.padding(4.dp).border(
        width = 0.5.dp,
        color = Color.Gray,
        shape = RoundedCornerShape(20.dp)
    ).padding(4.dp)) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally){
            CommonVerticalGap6()
            Text(text = "スケジュール　ロック", fontSize = 18.sp,fontWeight= FontWeight.Bold)
            LazyColumn {
                items(lockTimeList) { lockTimeData ->
                    LineDayOfWeekUnit(lockTimeData)
                }
            }
        }

    }

}

