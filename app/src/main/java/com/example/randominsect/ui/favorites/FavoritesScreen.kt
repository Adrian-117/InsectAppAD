package com.example.randominsect.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.example.randominsect.data.db.FavoriteInsect.InsectEntity
import java.io.File

private val DeleteRed = Color(0xFFD32F2F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val uriHandler = LocalUriHandler.current

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Favorites",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 3.dp
            ) {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(text = "Go back")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.favorites.isEmpty() -> {
                    EmptyFavorites(modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.favorites,
                            key = { it.id }
                        ) { insect ->
                            InsectCard(
                                insect = insect,
                                onDelete = { viewModel.deleteInsect(insect) },
                                onOpenWikipedia = { url ->
                                    try {
                                        uriHandler.openUri(normalizeUrl(url))
                                    } catch (_: Exception) {
                                        // No hay navegador disponible o la URL es inválida:
                                        // se ignora para no cerrar la app.
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavorites(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "🪲", fontSize = 56.sp)
        Text(
            text = "No saved insects yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Insects you mark as favorite will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InsectCard(
    insect: InsectEntity,
    onDelete: () -> Unit,
    onOpenWikipedia: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.padding(8.dp)) {
                InsectImage(
                    imagePath = insect.image_path,
                    contentDescription = insect.commonName ?: insect.scientificName
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = DeleteRed,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove from favorites",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = insect.scientificName?.takeIf { it.isNotBlank() }
                        ?: "Unknown scientific name",
                    style = MaterialTheme.typography.titleSmall,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = insect.commonName?.takeIf { it.isNotBlank() }
                        ?: "No common name",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                val wikiUrl = insect.wikipedia_url
                if (!wikiUrl.isNullOrBlank()) {
                    Text(
                        text = "Wikipedia",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { onOpenWikipedia(wikiUrl) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InsectImage(
    imagePath: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val frameModifier = modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))

    if (imagePath.isNullOrBlank()) {
        Box(modifier = frameModifier, contentAlignment = Alignment.Center) {
            Text(text = "🐛", fontSize = 40.sp)
        }
        return
    }

    val context = LocalContext.current
    val request = remember(imagePath) {
        ImageRequest.Builder(context)
            .data(File(imagePath))
            .crossfade(true)
            .build()
    }
    var loadFailed by remember(imagePath) { mutableStateOf(false) }

    Box(modifier = frameModifier, contentAlignment = Alignment.Center) {
        AsyncImage(
            model = request,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            onState = { state -> loadFailed = state is AsyncImagePainter.State.Error }
        )
        if (loadFailed) {
            Text(text = "🐛", fontSize = 40.sp)
        }
    }
}

/** Añade https:// si la URL guardada no incluye esquema. */
private fun normalizeUrl(url: String): String {
    val trimmed = url.trim()
    return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
        trimmed
    } else {
        "https://$trimmed"
    }
}
