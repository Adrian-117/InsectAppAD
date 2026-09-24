package com.example.randominsect.ui.settings

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.randominsect.data.ThemePreferences
import kotlinx.coroutines.launch

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBlacklist: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val isDarkModePref by ThemePreferences.isDarkMode.collectAsState(initial = null)
    val isDarkTheme = isDarkModePref ?: isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Settings Screen", color = MaterialTheme.colorScheme.onBackground)

        Row(
            modifier = Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode", color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { isChecked ->
                    coroutineScope.launch {
                        ThemePreferences.setDarkMode(isChecked)
                    }
                }
            )
        }

        Button(
            onClick = onNavigateToBlacklist,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Go to Blacklist")
        }

        Button(
            onClick = onNavigateBack,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Back")
        }
    }
}
