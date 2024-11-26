package com.atech.advancednotesremastered.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atech.advancednotesremastered.data.NoteDao
import com.atech.advancednotesremastered.data.NoteEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainScreenViewModel(private val dao: NoteDao) : ViewModel() {

    val notes = dao.getNotesOrdered().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun switchFavourite(note: NoteEntity) {
        viewModelScope.launch {
            dao.switchFavourite(note)
        }
    }
}