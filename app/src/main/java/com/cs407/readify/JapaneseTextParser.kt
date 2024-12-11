package com.cs407.readify
import com.atilika.kuromoji.TokenizerBase
import com.atilika.kuromoji.ipadic.Tokenizer

class JapaneseTextParser {
    private val tokenizer = Tokenizer.Builder()
        .mode(TokenizerBase.Mode.NORMAL)
        .build()

    /**
     * Segments Japanese text into meaningful tokens with their readings and parts of speech
     */
    fun parseText(text: String): List<JapaneseToken> {
        val tokens = tokenizer.tokenize(text)
        return tokens.map { token ->
            JapaneseToken(
                surface = token.surface,
                reading = token.reading,
                partOfSpeech1 = token.partOfSpeechLevel1,
                partOfSpeech2 = token.partOfSpeechLevel2,
                partOfSpeech3 = token.partOfSpeechLevel3,
                partOfSpeech4 = token.partOfSpeechLevel4,
                baseForm = token.baseForm,
                pronunciation = token.pronunciation,
                conjugationForm = token.conjugationForm,
                conjugationType = token.conjugationType

            )
        }
    }

    /**
     * Segments text into words without additional information
     */
    fun segmentText(text: String): List<String> {
        return tokenizer.tokenize(text).map { it.surface }
    }

    /**
     * Gets furigana readings for kanji words
     */
    fun getFurigana(text: String): List<FuriganaEntry> {
        return tokenizer.tokenize(text)
            .filter { it.reading != null && it.reading != it.surface }
            .map { token ->
                FuriganaEntry(
                    kanji = token.surface,
                    reading = token.reading
                )
            }

    }

     fun combineTokens(tokens: List<JapaneseToken>): List<JapaneseToken> {
        val result = mutableListOf<JapaneseToken>()
        var i = 0

        while (i < tokens.size) {
            // Get current token
            var combinedToken = tokens[i]

            // Look ahead for combinations
            while (i + 1 < tokens.size) {
                val nextToken = tokens[i + 1]

                if (shouldCombineTokens(combinedToken, nextToken)) {
                    combinedToken = combineTokenPair(combinedToken, nextToken)
                    i++ // Skip the token we just combined
                } else {
                    break // No more combinations possible
                }
            }

            result.add(combinedToken)
            i++
        }

        return result
    }

    private fun shouldCombineTokens(first: JapaneseToken, second: JapaneseToken): Boolean {
        // Case 1: Honorific お prefix with nouns
        if (first.surface == "お" &&
            first.partOfSpeech1 == "接頭詞" &&
            second.partOfSpeech1 == "名詞") {
            return true
        }


        // Case 2: Verb conjugation patterns
        if (first.partOfSpeech1 == "動詞") {
            // Combine with auxiliary verbs and certain particles
            if (second.partOfSpeech1 == "助動詞" ||
                second.partOfSpeech1 == "動詞" && second.partOfSpeech2 == "非自立" || second.partOfSpeech2 == "接続助詞")
                 {
                return true
            }

            // Handle てる、てい forms
            if (first.conjugationForm == "連用タ接続" &&
                second.surface in listOf("て", "た") ||
                second.baseForm in listOf("いる", "てる")) {
                return true
            }
        }

        return false
    }



    private fun combineTokenPair(first: JapaneseToken, second: JapaneseToken): JapaneseToken {
        return JapaneseToken(
                surface = first.surface + second.surface,
                reading = (first.reading ?: "") + (second.reading ?: ""),
                partOfSpeech1 = first.partOfSpeech1,
                partOfSpeech2 = first.partOfSpeech2,
                partOfSpeech3 = first.partOfSpeech3,
                partOfSpeech4 = first.partOfSpeech4,
                baseForm = first.baseForm, // + second.baseForm,
                pronunciation = first.pronunciation + second.pronunciation,
                conjugationForm = second.conjugationForm,
                conjugationType = first.conjugationType
            )
    }





}




// Data classes to hold parsed results
data class JapaneseToken(
    val surface: String,      // The actual text
    val reading: String?,     // Katakana reading
    val partOfSpeech1: String, // Grammatical role
    val partOfSpeech2: String, // Sub-role
    val partOfSpeech3: String, // Sub-sub-role
    val partOfSpeech4: String?,
    val baseForm: String? ,    // Dictionary form
    val pronunciation : String ,// Pronunciation
    val conjugationForm: String? = null, // Conjugation form
    val conjugationType: String? = null  // Conjugation type
)

data class FuriganaEntry(
    val kanji: String,
    val reading: String?
)


