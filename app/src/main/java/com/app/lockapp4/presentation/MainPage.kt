package com.app.lockapp4.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.lockapp4.framework.database.MainViewModel
import com.app.lockapp4.ui.LinesDayOfWeek
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

//https://blog.mokelab.com/
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavController) {

    GlobalScope.launch(Dispatchers.IO) {
        viewModel.makeInitialAllData()

    }

    Scaffold(floatingActionButton = {  }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text=viewModel.lockTimes.collectAsState(emptyList()).value.size.toString()+"データ")
//            BodyNextInfo(isLockedText)
            LinesDayOfWeek()
            Text(text = "Instant Lock")
//            Text(text = isLockedText)
            Button(
                onClick = {
//                    isInstantlyLockedCompanion = !isInstantlyLockedCompanion
//                    if (isInstantlyLockedCompanion) {
//                        isLockedText = "isLocked(instant)"
//                    } else {
//                        isLockedText = "isUnlocked"
//                    }
                },
            ) {
                Text(text = "Lock")
            }

        }
    }
}