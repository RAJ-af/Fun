package com.itsraj.funkytalk

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.itsraj.funkytalk.ui.navigation.MainAppScreen
import com.itsraj.funkytalk.ui.theme.FunkyTalkTheme
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FunkyTalkApp.supabase.handleDeeplinks(intent)

        enableEdgeToEdge()
        setContent {
            FunkyTalkTheme {
                MainAppScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        FunkyTalkApp.supabase.handleDeeplinks(intent)
    }
}
