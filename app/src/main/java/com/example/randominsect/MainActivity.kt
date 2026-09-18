package com.example.randominsect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.randominsect.navigation.AppNavigation
import com.example.randominsect.ui.theme.RandomInsectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RandomInsectTheme {
                AppNavigation()
            }
        }
    }
}
