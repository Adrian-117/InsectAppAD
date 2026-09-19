package com.example.randominsect.data.db.BlackList


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blacklist_taxa")
data class BlackListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "taxa")
    val taxa: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "i_naturalist_id")
    val iNaturalistId: Long,

    @ColumnInfo(name = "is_blacklisted")
    val isBlacklisted: Boolean = true
)


