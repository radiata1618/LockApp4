package com.app.lockapp4.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.lockapp4.framework.database.MainViewModel


@Composable
fun LinesDayOfWeek(
    viewModel: MainViewModel = hiltViewModel()
) {

    var lockTimeList = viewModel.lockTimes.collectAsState(emptyList()).value
    if (lockTimeList.isEmpty()) {
        Text(text = "Preparing")
    } else {
        LazyColumn {
            items(lockTimeList) { lockTimeData ->
                LineDayOfWeekUnit(lockTimeData)
            }
        }

    }
}

