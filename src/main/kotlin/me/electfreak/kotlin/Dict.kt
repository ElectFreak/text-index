package me.electfreak.kotlin

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

val pathToDictionary = "data/odict.csv"
val pathToOdict = "data/new_odict.csv"

/**
 * Uses csv dictionary for building textIndex.Trie with words.
     * Reads csv file by line and fill textIndex.Trie by every form of every word.
 *
 * @return textIndex.Trie with words and indices from dictionary.
 */
fun makeWordsTrie(pathToDictionary: String): Trie<Char> {
    val wordsTrie: Trie<Char> = Trie<Char>() // to be returned

    Files.newBufferedReader(Paths.get(pathToDictionary), Charset.forName("Windows-1251")).use { reader ->
        CSVParser(reader, CSVFormat.DEFAULT.withSkipHeaderRecord()).use { csvParser ->
            for (csvRecord in csvParser) {
//                 Accessing Values by Column Index
                csvRecord.filterIndexed { index, item -> index != 1 }.forEach { // skip second records – type of word
                    wordsTrie.apply {
                        insert(
                            it,
                            csvParser.recordNumber - 1
                        )
                    } // textIndex.insert word form to a textIndex.Trie with index – line number from dictionary
                }
            }
        }
    }

    return wordsTrie
}

val odict = File(pathToOdict).readLines()
/**
 * Gives word from dictionary by its index (if exist)
 *
 * @return word in its first form from dictionary or null if there is no word with such index.
 */
fun getWordByIndex(index: Long): String? {
    return odict[index.toInt()].split(",")[0]
}