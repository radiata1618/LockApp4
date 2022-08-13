package com.app.lockapp4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.lockapp4.presentation.LockTimeViewModel
import com.app.lockapp4.presentation.MainPage
import com.app.lockapp4.presentation.ui.LockApp4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreenUI()
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
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(navController = navController, startDestination = "MainPage") {
                composable("MainPage") {
                    val viewModel = hiltViewModel<LockTimeViewModel>()
                    MainPage(navController = navController,viewModel=viewModel)
                }
            }
        }
    }
}
