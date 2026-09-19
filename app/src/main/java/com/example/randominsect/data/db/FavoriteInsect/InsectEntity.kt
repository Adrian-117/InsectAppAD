package com.example.randominsect.data.db.FavoriteInsect

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_insects")
data class InsectEntity(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(name = "common_name")
    val commonName: String?,

    @ColumnInfo(name = "scientific_name")
    val scientificName: String?,

    val wikipedia_url: String? = null,

    // Local file path where saved photo lives on the phone
    val image_path: String? = null
)
