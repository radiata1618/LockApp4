package com.app.lockapp4.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.lockapp4.framework.database.LockTime
import com.app.lockapp4.framework.database.MainViewModel
import com.app.lockapp4.framework.utl.commonTranslateTimeIntToString
import com.app.lockapp4.presentation.theme.CommonColorSecondary
import com.app.lockapp4.presentation.theme.CommonColorTertiary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@Composable
fun LineDayOfWeekUnit(
    lockTimeData: LockTime,
    viewModel: MainViewModel = hiltViewModel()
) {
    // Value for storing time as a string
    val mTime = remember { mutableStateOf("") }
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .border(
                width = 0.5.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.width(90.dp), text = lockTimeData.dayName
            )
            Text(
                modifier = Modifier.clickable {
                    // Creating a TimePicker dialod
                    val fromTimePickerDialog = TimePickerDialog(
                        context,
                        { _, mHour: Int, mMinute: Int ->
                            viewModel.updateFromTime(lockTimeData.dayId, mHour, mMinute)
                        },
                        lockTimeData.fromTimeHour, lockTimeData.fromTimeMinute, false
                    )
                    fromTimePickerDialog.show()
                },
                text = commonTranslateTimeIntToString(
                    if (lockTimeData.fromTimeHour < 12) {
                        lockTimeData.fromTimeHour + 24
                    } else {
                        lockTimeData.fromTimeHour
                    },
                    lockTimeData.fromTimeMinute
                )
            )
            Text(text = "～")
            Text(text = "翌日")
            Text(
                modifier = Modifier.clickable {
                    // Creating a TimePicker dialod
                    val toTimePickerDialog = TimePickerDialog(
                        context,
                        { _, mHour: Int, mMinute: Int ->
                            viewModel.updateToTime(lockTimeData.dayId, mHour, mMinute)
                        },
                        lockTimeData.toTimeHour, lockTimeData.toTimeMinute, false
                    )
                    toTimePickerDialog.show()
                },
                text = commonTranslateTimeIntToString(
                    lockTimeData.toTimeHour,
                    lockTimeData.toTimeMinute
                )
            )
            DayOfWeekButtonUnit(lockTimeData)
        }

    }
}

@Composable
fun DayOfWeekButtonUnit(
    lockTime: LockTime,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .size(30.dp, 30.dp)
            .toggleable(
                value = lockTime.enableLock,
                onValueChange = {
                    viewModel.updateEnable(lockTime)
                }
            ),
        shape = CircleShape,
        color = if (lockTime.enableLock) CommonColorSecondary else CommonColorTertiary,
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (lockTime.enableLock) {
                    "ON"
                } else {
                    "OFF"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (lockTime.enableLock) Color.White else Color.Black,
            )
        }
    }
}
