package com.itsraj.funkytalk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.itsraj.funkytalk.ui.navigation.MainAppScreen
import com.itsraj.funkytalk.ui.theme.FunkyTalkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FunkyTalkTheme {
                MainAppScreen()
            }
        }
    }
}
