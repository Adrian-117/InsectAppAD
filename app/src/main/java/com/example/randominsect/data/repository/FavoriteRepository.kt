package com.example.randominsect.data.repository

import com.example.randominsect.data.db.FavoriteInsect.FavoritesDao
import com.example.randominsect.data.db.FavoriteInsect.InsectEntity
import com.example.randominsect.data.db.InsectDatabase
import com.example.randominsect.moveToPermanentStorage
import com.example.randominsect.data.AppContextProvider
import kotlinx.coroutines.flow.Flow
import java.io.File
import android.util.Log

class FavoriteRepository private constructor(
    private val favoritesDao: FavoritesDao
) {

    val allFavorites: Flow<List<InsectEntity>> = favoritesDao.getAllFavorites()

    suspend fun insert(insect: InsectEntity) {
        val context = AppContextProvider.get()
        val finalImagePath = insect.image_path?.let { currentPath ->
            val sourceFile = File(currentPath)
            
            if (sourceFile.exists() && sourceFile.absolutePath.contains(context.cacheDir.absolutePath)) {
                moveToPermanentStorage(sourceFile)
            } else {
                currentPath
            }
        }

        val updatedEntity = insect.copy(image_path = finalImagePath)
	Log.d("insectapp","Inserting insect with previous filepath ${insect.image_path} and new filepath ${updatedEntity.image_path}")
        favoritesDao.insertInsect(updatedEntity)
    }

    suspend fun insertAll(insects: List<InsectEntity>) {
        favoritesDao.insertAll(insects)
    }

    suspend fun update(insect: InsectEntity) {
        favoritesDao.updateInsect(insect)
    }

    suspend fun delete(insect: InsectEntity) {
        favoritesDao.deleteInsect(insect)
    }

    suspend fun deleteById(id: Long) {
        favoritesDao.deleteById(id)
    }

    suspend fun getById(id: Long): InsectEntity? {
        return favoritesDao.getInsectById(id)
    }

    suspend fun clearAll() {
        favoritesDao.clearAllFavorites()
    }

    suspend fun isTaxonFavorite(taxonID:Long): Boolean {
        return favoritesDao.isTaxonFavorite(taxonID)
    }
    

    companion object {
        @Volatile
        private var INSTANCE: FavoriteRepository? = null

        fun getInstance(): FavoriteRepository {
            return INSTANCE ?: synchronized(this) {
                val context = AppContextProvider.get()
                val database = InsectDatabase.getDatabase(context)
                val instance = FavoriteRepository(database.favoritesDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
