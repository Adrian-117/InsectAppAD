package com.example.randominsect.data.repository

import com.example.randominsect.data.AppContextProvider
import com.example.randominsect.data.db.BlackList.BlackListEntity
import com.example.randominsect.data.db.BlackList.BlacklistDao
import com.example.randominsect.data.db.InsectDatabase
import kotlinx.coroutines.flow.Flow

class BlacklistRepository private constructor(
    private val blacklistDao: BlacklistDao
) {

    /**
     * Observes all blacklisted items as a reactive Flow.
     * Useful for binding directly to Compose or View states.
     */
    val allBlacklistTaxa: Flow<List<BlackListEntity>> = blacklistDao.getAll()

    /**
     * Inserts a single blacklisted taxon item.
     */
    suspend fun insert(taxon: BlackListEntity) {
        blacklistDao.insert(taxon)
    }

    /**
     * Inserts a batch list of blacklisted taxon items.
     * Ignores conflicts for items that already exist.
     */
    suspend fun insertAll(taxa: List<BlackListEntity>) {
        blacklistDao.insertAll(taxa)
    }

    /**
     * Updates an existing blacklisted taxon item.
     */
    suspend fun update(taxon: BlackListEntity) {
        blacklistDao.update(taxon)
    }

    /**
     * Deletes a specific blacklisted taxon item from the database.
     */
    suspend fun delete(taxon: BlackListEntity) {
        blacklistDao.delete(taxon)
    }

    /**
     * Returns a snapshot list of all blacklisted taxa once (non-reactive).
     */
    suspend fun getDirectList(): List<BlackListEntity> {
        return blacklistDao.getDirectList()
    }

    /**
     * Retrieves comma-separated string of active iNaturalist IDs (e.g., "81769,52747,49553").
     * Ideal for passing into API queries like Ktor's `without_taxon_id`.
     */
    suspend fun getBlacklistQueryString(): String? {
        return blacklistDao.getBlacklistQueryString()
    }

    companion object {
        @Volatile
        private var INSTANCE: BlacklistRepository? = null

        fun getInstance(): BlacklistRepository {
            return INSTANCE ?: synchronized(this) {
                val context = AppContextProvider.get()
                val database = InsectDatabase.getDatabase(context)
                val instance = BlacklistRepository(database.blacklistDao())
                INSTANCE = instance
                instance
            }
        }
    }
}
