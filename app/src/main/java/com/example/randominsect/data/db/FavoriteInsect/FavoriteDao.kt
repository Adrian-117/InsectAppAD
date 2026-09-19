package com.example.randominsect.data.db.FavoriteInsect

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsect(insect: InsectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(insects: List<InsectEntity>)

    @Update
    suspend fun updateInsect(insect: InsectEntity)

    @Delete
    suspend fun deleteInsect(insect: InsectEntity)

    @Query("DELETE FROM favorite_insects WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM favorite_insects ORDER BY id DESC")
    fun getAllFavorites(): Flow<List<InsectEntity>>

    @Query("SELECT * FROM favorite_insects WHERE id = :id LIMIT 1")
    suspend fun getInsectById(id: Long): InsectEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_insects WHERE id = :id)")
    suspend fun isFavorited(id: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_insects WHERE id = :id)")
    fun observeIsFavorited(id: Long): Flow<Boolean>

    @Query("DELETE FROM favorite_insects")
    suspend fun clearAllFavorites()
}
