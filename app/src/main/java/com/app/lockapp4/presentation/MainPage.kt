package com.app.lockapp4.presentation

import android.annotation.SuppressLint
import android.widget.NumberPicker
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.lockapp4.framework.database.MainViewModel
import com.app.lockapp4.framework.utl.stringIsLocked
import com.app.lockapp4.framework.utl.stringIsUnlocked
import com.app.lockapp4.ui.HeaderInfo
import com.app.lockapp4.ui.LinesDayOfWeek
import com.app.lockapp4.ui.MyNumberField
import com.chillibits.composenumberpicker.HorizontalNumberPicker
import com.chillibits.composenumberpicker.VerticalNumberPicker
import timber.log.Timber

//https://blog.mokelab.com/
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavController) {

    viewModel.makeInitialAllData()
    viewModel.getNextToTimeIfScheduledLocking()
    val context = LocalContext.current
    val instantLockData = viewModel.instantLock.collectAsState(emptyList()).value

    Scaffold(floatingActionButton = {  }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderInfo()
            LinesDayOfWeek()
            Text(text = "Instant Lock")
            Text(text = if(instantLockData.isEmpty()){stringIsUnlocked}else{stringIsLocked})
            Row {
                MyNumberField(navController=navController)
                Text(text = "分")
                Button(
                    onClick = {
                        viewModel.insertInstantLock()
                    },
                ) {
                    Text(text = "Lock")
                }
            }

            Button(
                onClick = {
                    viewModel.deleteInstantLock()
                },
            ) {
                Text(color= Color.Red,text = "Emergency unlock")
            }

        }
    }
}