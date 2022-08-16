package com.app.lockapp4

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.Nullable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.lockapp4.framework.utl.requestPermissionCode
import com.app.lockapp4.ui.MainPage
import com.app.lockapp4.presentation.theme.LockApp4Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreenUI()
        }

        requestPermission()
    }

    override fun onStart() {
        super.onStart()

        GlobalScope.launch(Dispatchers.IO) {
            cancelRestartPlan(applicationContext)
            insertDefaultLockTimeData(applicationContext)
            recalculateNextOrDuringLockTime(applicationContext)
            deleteInstantLockData(applicationContext)
            scheduleForDuringForeground(applicationContext)
        }
    }

    override fun onDestroy() {
        Log.d("■■■■■■■■■■■", "onDestroyが呼び出される")
        super.onDestroy()
        restartAppOrScheduleRestart(applicationContext)
    }

    override fun onStop() {
        Log.d("■■■■■■■■■■■", "onStopが呼び出される")
        super.onStop()
        restartAppOrScheduleRestart(applicationContext)
    }

    // SYSTEM_ALERT_WINDOWが許可されているかのチェック
    fun isGranted(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    // SYSTEM_ALERT_WINDOWの許可をリクエストする
    private fun requestPermission() {
        if (Settings.canDrawOverlays(this)) {
            // 許可されたときの処理
        } else {
            val uri: Uri = Uri.parse("package:$packageName")
            val intent: Intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, uri)
            startActivityForResult(intent, requestPermissionCode)
        }
    }

    // 許可されたかの確認は、onActivityResultでチェックする
    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        if (requestCode == requestPermissionCode) {
            if (Settings.canDrawOverlays(this)) {
                // 許可されたときの処理
            } else {
                // 拒否されたときの処理
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenUI(){
    LockApp4Theme {

        val navController = rememberNavController()

        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize()
//                .background(
//                Brush.linearGradient(
//                    colors=listOf(BackGroundBlueColor1, BackGroundBlueColor2)
//                ))
            ,
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(navController = navController, startDestination = "MainPage") {
                composable("MainPage") {
//                    val viewModel = LockTimeViewModel(Application())
                    MainPage(navController = navController)
                }
            }
        }
    }
}
