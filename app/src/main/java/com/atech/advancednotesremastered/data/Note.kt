package com.atech.advancednotesremastered.data

import androidx.compose.ui.graphics.Color

class Note(
    title: String,
    var text: String,
    var color: Color,
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

    private fun getTitleFromText(): String {
        return text.substring(
            0, minOf(
                20, text.length,
                if (text.contains('\n')) text.indexOf('\n') else 20
            )
        )
    }
}