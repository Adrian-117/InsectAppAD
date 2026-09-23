package com.example.randominsect.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.randominsect.ui.theme.DarkNavy
import com.example.randominsect.ui.theme.DeepNavy
import com.example.randominsect.ui.theme.SlateDivider
import com.example.randominsect.ui.theme.TealGreen
import java.io.File

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val insect by viewModel.generatedInsect.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepNavy)
                .padding(top = 40.dp, bottom = 16.dp), // Adjust for status bar space
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Main Menu",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(color = SlateDivider, thickness = 1.dp)

        // Generate Button Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { viewModel.generateRandomInsect() },
                colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text(
                    text = if (isLoading) "Generating..." else "Generate",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        
        HorizontalDivider(color = SlateDivider, thickness = 1.dp)

        // Center Content
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (insect == null) {
                Text(
                    text = "No Animal Has Been Generated",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Display image if image_path exists
                    insect?.image_path?.let { path ->
                        val imageFile = File(path)
                        if (imageFile.exists()) {
                            AsyncImage(
                                model = imageFile,
                                contentDescription = insect?.commonName ?: "Insect Image",
                                modifier = Modifier
                                    .size(240.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(DeepNavy),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Text(
                        text = insect?.commonName ?: insect?.scientificName ?: "Unknown Insect",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    if (insect?.commonName != null && insect?.scientificName != null) {
                        Text(
                            text = insect?.scientificName ?: "",
                            color = Color.LightGray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        // Navigation Footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = onNavigateToFavorites,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy)
            ) {
                Text("Favorites", color = Color.White)
            }
            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepNavy)
            ) {
                Text("Settings", color = Color.White)
            }
        }
    }
}
