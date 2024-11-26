package com.atech.advancednotesremastered.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Upsert
    suspend fun upsertNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNote(id: Int): NoteEntity

    @Query("SELECT * FROM notes ORDER BY date DESC")
    fun getNotesOrdered(): Flow<List<NoteEntity>>

    @Query("UPDATE notes SET isFavourite = :isFavourite WHERE id = :id")
    suspend fun updateFavourite(isFavourite: Boolean, id: Int)

    @Transaction
    suspend fun switchFavourite(note: NoteEntity) {
        updateFavourite(!note.isFavourite, note.id)
    }
}