package com.app.lockapp4.ui

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.lockapp4.framework.database.MainViewModel
import com.app.lockapp4.judgeNowLockedReturnUnlockTime
import java.util.*

@Composable
fun MyNumberField(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavController

) {
    val endTimeCal: Calendar? = judgeNowLockedReturnUnlockTime(
        viewModel.instantLock.collectAsState(emptyList()).value,
        viewModel.nextOrDuringLockTime.collectAsState(emptyList()).value
    )

    val change : (String) -> Unit = { it ->
        viewModel.instantLockTimeOnScreenMinuteInInt = if(it==""){0}else{it.toInt()}
    }

    TextField(
        value = viewModel.instantLockTimeOnScreenMinuteInInt.toString(),
        modifier = Modifier.width(40.dp).height(50.dp),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onValueChange = change,
        enabled = endTimeCal==null
    )

}
