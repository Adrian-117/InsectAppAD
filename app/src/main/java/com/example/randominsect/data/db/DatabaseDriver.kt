package com.example.randominsect.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope

import com.example.randominsect.data.db.FavoriteInsect.InsectEntity
import com.example.randominsect.data.db.FavoriteInsect.FavoritesDao
import com.example.randominsect.data.db.BlackList.BlackListEntity
import com.example.randominsect.data.db.BlackList.BlacklistDao

import android.util.Log

@Database(
    entities = [
        BlackListEntity::class,
        InsectEntity::class,        
    ],
    version = 5, // Version bump forces schema reset
    exportSchema = false
)
abstract class InsectDatabase : RoomDatabase() {

    abstract fun blacklistDao(): BlacklistDao
    abstract fun favoritesDao(): FavoritesDao

    companion object {
        @Volatile
        private var INSTANCE: InsectDatabase? = null

        fun getDatabase(context: Context): InsectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    InsectDatabase::class.java,
                    "insect_database"
                )
                .addCallback(InsectDatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }
    }

    private class InsectDatabaseCallback : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            
	    Log.d("insectapp","Here executing sql")
            // Matches singular 'blacklist_taxa' table name
            db.execSQL(
                """
                INSERT INTO blacklist_taxa (i_naturalist_id, description, taxa, is_blacklisted) VALUES
                (81769, 'Cockroaches and Termites', 'Order Blattodea', 0),
                (52747, 'Hornets, Paper Wasps, Potter Wasps, and Allies', 'Family Vespidae', 0),
                (49553, 'Long-legged Centipedes', 'Order Scutigeromorpha', 0),
                (48894, 'Scorpions', 'Order Scorpiones', 0),
                (47118, 'Spiders', 'Order Araneae', 0)
                """.trimIndent()
            )
        }
    }
}
