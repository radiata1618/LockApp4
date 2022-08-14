package com.app.lockapp4.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.lockapp4.framework.database.MainViewModel

@Composable
fun MyNumberField(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavController

) {

    val change : (String) -> Unit = { it ->
        viewModel.instantLockTimeOnScreen = if(it==""){0}else{it.toInt()}
    }

    TextField(
        value = viewModel.instantLockTimeOnScreen.toString(),
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        onValueChange = change
    )

}
