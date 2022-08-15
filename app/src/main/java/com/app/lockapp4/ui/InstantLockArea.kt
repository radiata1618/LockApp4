package com.app.lockapp4.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.lockapp4.framework.database.MainViewModel
import com.app.lockapp4.presentation.common.CommonVerticalGap16
import com.app.lockapp4.presentation.common.CommonVerticalGap6

@Composable
fun InstantLockArea(
    viewModel: MainViewModel = hiltViewModel(),
    navController: NavController
){

    val context = LocalContext.current
    Box(modifier = Modifier.padding(4.dp).border(
        width = 0.5.dp,
        color = Color.Gray,
        shape = RoundedCornerShape(20.dp)
    ).fillMaxWidth()) {
        Column(
            modifier =  Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally){
            CommonVerticalGap6()
            Text(text = "インスタント　ロック", fontSize = 18.sp,fontWeight= FontWeight.Bold)
            CommonVerticalGap6()
            Row(
                verticalAlignment = Alignment.CenterVertically) {
                MyNumberField(navController=navController)
                Text(text = "分間")
                Button(
                    onClick = {
                        viewModel.insertInstantLock(context)
                    },
                ) {
                    Text(text = "ロック")
                }
            }
            CommonVerticalGap16()
        }
    }
}