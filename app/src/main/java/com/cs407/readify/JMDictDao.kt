package com.cs407.readify

import androidx.room.Dao
import androidx.room.Query

@Dao
interface JMDictDao {
    @Query("SELECT * FROM entries WHERE reading = :reading LIMIT 3")
    suspend fun findByReading(reading: String): List<JMDictEntry>

    @Query("SELECT * FROM entries WHERE kanji = :kanji LIMIT 3")
    suspend fun findByKanji(kanji: String): List<JMDictEntry>

    // For fuzzy search, you might use LIKE queries:
    @Query("SELECT * FROM entries WHERE reading LIKE '%' || :query || '%' OR kanji LIKE '%' || :query || '%' LIMIT 3")
    suspend fun search(query: String): List<JMDictEntry>
}