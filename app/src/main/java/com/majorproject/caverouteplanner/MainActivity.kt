package com.majorproject.caverouteplanner

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.majorproject.caverouteplanner.ui.screens.MapScreen
import com.majorproject.caverouteplanner.ui.theme.CaveRoutePlannerTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaveRoutePlannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MapScreen()
                }
            }
        }
    }
}