package com.atech.advancednotesremastered.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.advancednotesremastered.data.Note
import com.atech.advancednotesremastered.data.NoteDao
import com.atech.advancednotesremastered.data.NoteEntity
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.random.Random

class EditorScreenViewModel(private val dao: NoteDao) : ViewModel() {

    private var noteId: Int? = null

    var title by mutableStateOf("")
    var text by mutableStateOf("")
    var color by mutableStateOf(Color.Blue)
    var isFavourite by mutableStateOf(false)

    fun setContent(id: Int?) {
        clearContent()
        viewModelScope.launch {
            noteId = id
            if (id != null) {
                val note: NoteEntity = dao.getNote(id)
                title = note.title
                text = note.text
                color = Color(note.color)
                isFavourite = note.isFavourite
            }
        }
    }

    private fun clearContent() {
        val rnd = Random.Default
        noteId = null
        title = ""
        text = ""
        color = Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256), 255)
        isFavourite = false
    }

    fun upsert() {
        val note = Note(
            title,
            text,
            color,
            LocalDateTime.now(),
            isFavourite
        )
        viewModelScope.launch {
            if (noteId == null) {
                dao.upsertNote(note.toEntity())
            } else {
                dao.upsertNote(note.toEntity(noteId!!))
            }
        }
    }

    fun delete() {
        if (noteId != null) {
            viewModelScope.launch {
                dao.deleteNote(dao.getNote(noteId!!))
            }
        }
    }
}