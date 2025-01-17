package com.atech.advancednotesremastered.data

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.LocalDateTime
import java.time.ZoneOffset

class Note(
    title: String,
    var text: String,
    var color: Color,
    var image: Uri,
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
        Uri.parse(noteEntity.image),
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
            image.toString(),
            date.toEpochSecond(ZoneOffset.UTC),
            isFavourite
        )
    }

    fun toEntity(id: Int): NoteEntity {
        return NoteEntity(
            title,
            text,
            color.toArgb(),
            image.toString(),
            date.toEpochSecond(ZoneOffset.UTC),
            isFavourite,
            id
        )
    }
}