package com.example.randominsect.ui.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.randominsect.data.AppContextProvider
import com.example.randominsect.data.db.FavoriteInsect.InsectEntity
import com.example.randominsect.data.repository.FavoriteRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Estado de la pantalla de favoritos.
 *
 * [isLoading] evita que se muestre el mensaje de "sin favoritos" durante
 * el instante en que Room todavía no ha emitido su primera lista.
 */
data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favorites: List<InsectEntity> = emptyList()
)

class FavoritesViewModel(
    private val repository: FavoriteRepository = FavoriteRepository.getInstance()
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = repository.allFavorites
        .map { list -> FavoritesUiState(isLoading = false, favorites = list) }
        .catch { error ->
            Log.e(TAG, "Error observing favorites", error)
            emit(FavoritesUiState(isLoading = false, favorites = emptyList()))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = FavoritesUiState(isLoading = true)
        )

    /**
     * Elimina el insecto de la base de datos y, después, su imagen local.
     * La lista de la UI se actualiza sola porque Room re-emite el Flow.
     */
    fun deleteInsect(insect: InsectEntity) {
        viewModelScope.launch {
            try {
                repository.delete(insect)
                deleteImageFileIfUnused(insect)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting favorite with id ${insect.id}", e)
            }
        }
    }

    /** Variante para eliminar directamente por id. */
    fun deleteInsectById(id: Long) {
        viewModelScope.launch {
            try {
                val insect = repository.getById(id)
                if (insect != null) {
                    repository.delete(insect)
                    deleteImageFileIfUnused(insect)
                } else {
                    repository.deleteById(id)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting favorite with id $id", e)
            }
        }
    }

    /**
     * Borra el archivo de imagen para no dejar huérfanos en el almacenamiento.
     *
     * Solo se borra si:
     *  - el archivo está dentro de filesDir/saved_insects (la carpeta que crea
     *    `moveToPermanentStorage`), nunca fuera de ella; y
     *  - ningún otro favorito sigue apuntando a esa misma ruta.
     */
    private suspend fun deleteImageFileIfUnused(insect: InsectEntity) {
        val path = insect.image_path ?: return

        val stillReferenced = repository.allFavorites.first().any { it.image_path == path }
        if (stillReferenced) return

        withContext(Dispatchers.IO) {
            val savedDir = File(AppContextProvider.get().filesDir, SAVED_IMAGES_DIR).canonicalFile
            val imageFile = File(path).canonicalFile
            if (imageFile.parentFile == savedDir && imageFile.exists()) {
                if (!imageFile.delete()) {
                    Log.w(TAG, "Could not delete image file: ${imageFile.absolutePath}")
                }
            }
        }
    }

    private companion object {
        const val TAG = "insectapp"
        const val SAVED_IMAGES_DIR = "saved_insects"
    }
}
