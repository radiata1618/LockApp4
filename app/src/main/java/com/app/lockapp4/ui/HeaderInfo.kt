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
import com.app.lockapp4.judgeNowLockedReturnUnlockTime
import com.app.lockapp4.presentation.common.CommonVerticalGap16
import java.util.*


@Composable
fun HeaderInfo(
    viewModel: MainViewModel = hiltViewModel()
) {

    val endTimeCal: Calendar? = judgeNowLockedReturnUnlockTime(
        viewModel.instantLock.collectAsState(emptyList()).value,
        viewModel.nextOrDuringLockTime.collectAsState(emptyList()).value
    )

    if (endTimeCal == null) {

        CommonVerticalGap16()
        LockIcon(false)
        CommonVerticalGap16()
    } else {

        CommonVerticalGap16()
        LockIcon(true)
        CommonVerticalGap16()
        Text(text = stringIsLocked, fontSize = 36.sp)
        UnlockDateTime(endTimeCal)
        EmergencyButton()
    }
}

@Composable
fun EmergencyButton(
    viewModel: MainViewModel = hiltViewModel()
) {

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
        Text(color = Color.White, text = "緊急解除", fontSize = 20.sp, fontWeight = FontWeight.Bold)
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
                        if (viewModel.isEmergencyUnlocked) {
                            result = "OK"
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White,
                    )
                ) {

                    Text(text = "残り" + (constEmergencyTapNumberRequired - viewModel.emergencyTapNumber).toString() + "回")
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
                Text("ロック解除のために、" + constEmergencyTapNumberRequired.toString() + "回ボタンをタップしてください")
            },
        )
    }
}

@Composable
fun UnlockDateTime(cal: Calendar) {

    Text(text = "解除日時：${commonTranslateCalendarToStringYYYYMMDDHHMM(cal)}")
}

@Composable
fun LockIcon(isLocked: Boolean) {

    if (isLocked) {
        Icon(
            painter = rememberVectorPainter(image = Icons.Default.Lock),
            contentDescription = null,
            modifier = Modifier
                .height(40.dp)
                .width(40.dp)
        )
    } else {
        Icon(
            painter = rememberVectorPainter(image = Icons.Default.LockOpen),
            contentDescription = null,
            modifier = Modifier
                .height(40.dp)
                .width(40.dp)
        )

    }
}