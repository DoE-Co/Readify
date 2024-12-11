package com.cs407.readify

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class JMDictEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val kanji: String?,
    val reading: String,
    val gloss: String
)
