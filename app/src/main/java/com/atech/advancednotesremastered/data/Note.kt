package com.atech.advancednotesremastered.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.LocalDateTime
import java.time.ZoneOffset

class Note(
    title: String,
    var text: String,
    var color: Color,
    var date: LocalDateTime,
    var isFavourite: Boolean = false
) {
    var title = title.ifEmpty {
        getTitleFromText()
    }
        set(value) {
            field = value.ifEmpty {
                getTitleFromText()
            }
        }

    constructor(noteEntity: NoteEntity) : this(
        noteEntity.title,
        noteEntity.text,
        Color(noteEntity.color),
        LocalDateTime.ofEpochSecond(noteEntity.date, 0, ZoneOffset.UTC),
        noteEntity.isFavourite
    )

    private fun getTitleFromText(): String {
        return text.substring(
            0, minOf(
                20, text.length,
                if (text.contains('\n')) text.indexOf('\n') else 20
            )
        )
    }

    fun toEntity(): NoteEntity {
        return NoteEntity(
            title,
            text,
            color.toArgb(),
            date.toEpochSecond(ZoneOffset.UTC),
            isFavourite
        )
    }

    fun toEntity(id: Int): NoteEntity {
        return NoteEntity(
            title,
            text,
            color.toArgb(),
            date.toEpochSecond(ZoneOffset.UTC),
            isFavourite,
            id
        )
    }
}