package com.atech.advancednotesremastered.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.advancednotesremastered.data.NoteDao
import com.atech.advancednotesremastered.data.NoteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(private val dao: NoteDao) : ViewModel() {

    var search by mutableStateOf("")
    val notes = dao.searchNotes(search).stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun switchFavourite(note: NoteEntity) {
        viewModelScope.launch {
            dao.switchFavourite(note)
        }
    }
}