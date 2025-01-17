package com.atech.advancednotesremastered.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "notes")
data class NoteEntity(
    var title: String,
    var text: String,
    var color: Int,
    var image: String,
    var date: Long,
    var isFavourite: Boolean,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)