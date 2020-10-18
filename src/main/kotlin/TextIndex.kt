import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File

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
 * Gives a [TextIndex] by some text which represented as List<String>
 *
 * @param lines text parsed to a list of it lines.
 * @return [TextIndex].
 */
fun getTextIndexFromText(lines: List<String>): TextIndex {
    val formattedLines: List<List<String>> =
        lines
        .filter { it.trim().isNotEmpty() } // delete empty lines from list
        .map { line -> line
                .trim()
                .split(" ") // split line to array by space
                .map { word -> word.filter { it.isLetter() }.toLowerCase() } // make words contain only letters & toLowerCase
                .filter { word -> word.isNotEmpty() } // delete empty words
        }

    val wordsInfo = mutableMapOf<Long, MutableList<Word>>() // to be returned

    formattedLines.forEachIndexed { lineNumber, line ->
        for (word in line) {
            val indexInDictionary: Long = wordsTrie.getIndexOrNull(word) ?: continue // work with index of word or go to next word
            val wordWithInfo = Word(word, lineNumber) // to be added to wordsInfo

            if (wordsInfo[indexInDictionary] == null) {
                wordsInfo[indexInDictionary] = mutableListOf<Word>(wordWithInfo) // there are no words with this index yet, create new list
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
fun getTextIndexFromJson(json: File): TextIndex {
    val parsedJson: TextIndex = Gson().fromJson<TextIndex>(
        json.readLines().joinToString("\n"),
        TextIndex::class.java
    )

    return parsedJson
}

fun mostOftenMetWords(number: Int, textIndex: TextIndex): List<Long> {
    val numberOfWords = mutableListOf<Pair<Long, Int>>()

    textIndex.wordsInfo.forEach { (index: Long, wordsList: List<Word>) ->
        numberOfWords.add(Pair(index, wordsList.size))
    }

    numberOfWords.sortBy { it.second }

    val mostOftenMetWords: MutableList<Long> = mutableListOf()

    for (i in 1..number) {
        mostOftenMetWords.add(numberOfWords[numberOfWords.size - i].first)
    }

    return mostOftenMetWords
}