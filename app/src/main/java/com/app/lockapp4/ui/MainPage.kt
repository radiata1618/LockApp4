package com.app.lockapp4.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.lockapp4.framework.database.MainViewModel
import com.app.lockapp4.framework.utl.stringIsLocked
import com.app.lockapp4.framework.utl.stringIsUnlocked
import com.app.lockapp4.presentation.common.CommonVerticalGap16
import com.app.lockapp4.presentation.theme.BackGroundBlueColor1
import com.app.lockapp4.presentation.theme.BackGroundBlueColor2
import com.app.lockapp4.ui.HeaderInfo
import com.app.lockapp4.ui.LinesDayOfWeek
import com.app.lockapp4.ui.MyNumberField


@SuppressLint("CoroutineCreationDuringComposition", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavController) {

    val context = LocalContext.current

    viewModel.initialProcess()

    Scaffold() {
        var lockTimeList = viewModel.lockTimes.collectAsState(emptyList()).value
        if (lockTimeList.isEmpty()) {
            Text(text = "Preparing")
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeaderInfo()
                LinesDayOfWeek()
                CommonVerticalGap16()
                InstantLockArea(navController=navController)
            }
        }
    }
}