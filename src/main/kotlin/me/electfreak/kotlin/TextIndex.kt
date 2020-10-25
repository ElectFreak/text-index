package me.electfreak.kotlin

import com.github.ajalt.clikt.output.TermUi.echo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.lang.Exception

/**
 * Represents a single word from text in a TextIndex.
 * Contains word form, line from original text (where met) and calculate a page number.
 *
 * @property wordForm word form from original text.
 * @property lineNumber line number where word was met.
 * @property pageNumber page number (depends on lineNumber since there are 45 lines in a page) where word was met.
 */
data class Word(val wordForm: String, val lineNumber: Int) {
    val pageNumber = lineNumber / 45 + 1
}

/**
 * Represents a index of a text file.
 * Contains info about every word from original text (list of [Word]'s).
 *
 * @property wordsInfo map where every index of word (line number from dictionary) in dictionary mapped to a list of [Word]'s.
 */
data class TextIndex(
    var wordsInfo: MutableMap<Long, MutableList<Word>>
)

/**
 * Removes empty lines (and items containing only spaces) from List<String>
 *
 * @param lines lines of text.
 * @return not empty lines from [lines].
 */
fun removeEmptyLines(lines: List<String>): List<String> {
    return lines.filter { it.trim().isNotEmpty() }
}

/**
 * Gives a [TextIndex] by some text which represented as List<String>
 *
 * @param lines text parsed to a list of it lines.
 * @return [TextIndex].
 */
fun getTextIndexFromText(lines: List<String>): TextIndex {
    val formattedLines: List<List<String>> =
        removeEmptyLines(lines) // delete empty lines from list
            .map { line ->
                line
                    .trim()
                    .split(" ") // split line to array by space
                    .map { word ->
                        word.filter { it.isLetter() }.toLowerCase()
                    } // make words contain only letters & toLowerCase
                    .filter { word -> word.isNotEmpty() } // delete empty words
            }

    val wordsInfo = mutableMapOf<Long, MutableList<Word>>() // to be returned

    formattedLines.forEachIndexed { lineNumber, line ->
        for (word in line) {
            val indexInDictionary: Long =
                wordsTrie.getIndexOrNull(word) ?: continue // work with index of word or go to next word
            val wordWithInfo = Word(word, lineNumber) // to be added to wordsInfo

            if (wordsInfo[indexInDictionary] == null) {
                wordsInfo[indexInDictionary] =
                    mutableListOf<Word>(wordWithInfo) // there are no words with this index yet, create new list
            } else {
                wordsInfo[indexInDictionary]!!.add(wordWithInfo) // there are definitely some words with this index, checked earlier
            }

        }
    }

    return TextIndex(wordsInfo)
}

/**
 * Writes a [TextIndex] to a JSON file
 *
 * @param index [TextIndex] example to be written to JSON file.
 * @param file  file where [TextIndex] should be written.
 */
fun writeTextIndexToJsonFile(index: TextIndex, file: File) {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val json = gson.toJson(index)
    file.writeText(json)
}

/**
 * Gives a [TextIndex] from a JSON file
 *
 * @param json JSON file which contains [TextIndex].
 * @return [TextIndex].
 */
fun getTextIndexFromJson(json: File): TextIndex? {
    return try {
        Gson().fromJson<TextIndex>(
            json.readText(),
            TextIndex::class.java
        )
    } catch(e: Exception) {
        echo("Error in parsing text index: ")
        echo(e.message)
        null
    }
}

/**
 * Gives most often met words by number and working with [TextIndex]
 *
 * @param number how many words to give.
 * @return list of word indices in dictionary
 */
fun getMostOftenMetWords(number: Int, textIndex: TextIndex): List<Long> {
    if (number >= textIndex.wordsInfo.size) {
        return textIndex.wordsInfo.keys.toList()
    }

    val numberOfWords = mutableListOf<Pair<Long, Int>>() // Pair of index and number of word's with this index

    textIndex.wordsInfo.forEach { (index: Long, wordsList: List<Word>) ->
        numberOfWords.add(Pair(index, wordsList.size)) // Fill for sorting
    }

    numberOfWords.sortBy { it.second } // Sorting by number of words in text

    val mostOftenMetWords: MutableList<Long> = mutableListOf() // To be returned

    for (i in 1..number) {
        mostOftenMetWords.add(numberOfWords[numberOfWords.size - i].first) // Go from biggest number to smallest
    }

    return mostOftenMetWords
}