import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import writeTextIndexToJsonFile
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

val pathToDictionary = "data/odict.csv"
val pathToOdict = "data/new_odict.csv"
val wordsTrie = makeWordsTrie(pathToDictionary)

class Input : CliktCommand() {
    val number: Int? by option(help="Count of most often met words").int()

    val text: File? by option(help="Path to txt file")
        .file(mustExist = true, canBeDir = false, mustBeReadable = true)
    val output: File? by option(help="Path to JSON file for writing text index")
        .file(mustExist = false, canBeDir = false, mustBeWritable = true)
    val from: File? by option(help="Path to JSON file with text index for working with")
        .file(mustExist = true, canBeDir = false, mustBeReadable = true)
    val printAllLines: String? by option(help="The word by which all lines containing it will be displayed")
    val infoAbout: String? by option(help="Print info about this word")


    override fun run() {

        // Mode 1: create text index

        if (output != null && text == null) {
            echo("Path to text (--text) is required for creating text index")
        }

        if (output != null && text != null) {
            try {
                writeTextIndexToJsonFile(getTextIndexFromText(text!!.readLines()), output!!)
            } catch (e: Exception) {
                echo("Error:")
                echo(e.message)
            }
        }

        // Mode 2: working with text index

        if (number != null && from == null) {
            echo("Path to json file (--from) is required for working with text index")
        }

        if (number != null && from != null) {
            val textIndex = getTextIndexFromJson(from!!)
            for (index in mostOftenMetWords(number!!, textIndex)) {
                echo(getWordByIndex(index).split(",")[0])
            }
        }

        if (infoAbout != null && from != null) {
            val textIndex = getTextIndexFromJson(from!!)
            val wordIndex = wordsTrie.getIndexOrNull(infoAbout!!)
            val wordsList = textIndex.wordsInfo[wordIndex]!!
            echo("Word $infoAbout was met ${wordsList.size} times")
            for (word in wordsList) {
                echo("On page ${word.pageNumber} in form ${word.wordForm}")
            }
        }

        // Mode 3: print all lines containing the word

        if (printAllLines != null && text != null) {
            val textIndex = getTextIndexFromText(text!!.readLines())
            val textLines = removeEmptyLines(text!!.readLines())
            val wordIndex = wordsTrie.getIndexOrNull(printAllLines!!)
            textIndex.wordsInfo[wordIndex]?.forEach { word ->
                echo(textLines[word.lineNumber])
            }
        }
    }
}

fun main(args: Array<String>) = Input().main(args)

fun makeWordsTrie(pathToDictionary: String): Trie<Char> {
    val wordsTrie: Trie<Char> = Trie<Char>() // to be returned

    Files.newBufferedReader(Paths.get(pathToDictionary), Charset.forName("Windows-1251")).use { reader ->
        CSVParser(reader, CSVFormat.DEFAULT.withSkipHeaderRecord()).use { csvParser ->
            for (csvRecord in csvParser) {
//                 Accessing Values by Column Index
                csvRecord.filterIndexed { index, item -> index != 1 }.forEach { // skip second records – type of word
                    wordsTrie.apply { insert(it, csvParser.recordNumber - 1) } // insert word form to a Trie with index – line number from dictionary
                }
            }
        }
    }

    return wordsTrie
}

fun getWordByIndex(index: Long): String {
    return File(pathToOdict).readLines()[index.toInt()]
}