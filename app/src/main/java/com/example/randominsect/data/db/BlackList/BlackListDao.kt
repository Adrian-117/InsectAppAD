package com.example.randominsect.data.db.BlackList

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface BlacklistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(taxa: List<BlackListEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taxon: BlackListEntity)

    @Update
    suspend fun update(taxon: BlackListEntity)

    @Delete
    suspend fun delete(taxon: BlackListEntity)

    // Gets all items as a reactive Flow for your Compose UI
    @Query("SELECT * FROM blacklist_taxa")
    fun getAll(): Flow<List<BlackListEntity>>

    // Combines all active blacklisted IDs into a single string (e.g., "81769,52747,49553")
    // ready to pass directly to Ktor's `without_taxon_id` parameter
    @Query("SELECT GROUP_CONCAT(i_naturalist_id, ',') FROM blacklist_taxa WHERE is_blacklisted = 1")
    suspend fun getBlacklistQueryString(): String?

    @Query("SELECT * FROM blacklist_taxa")
    suspend fun getDirectList(): List<BlackListEntity>

}
