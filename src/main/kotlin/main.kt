import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths


val wordsTrie = makeWordsTrie(pathToDictionary)

class Input : CliktCommand() {

    // Mode 1: create text index

    val textPath: File? by option(help = "Path to txt file")
        .file(mustExist = true, canBeDir = false, mustBeReadable = true)
    val output: File? by option(help = "Path to JSON file for writing text index")
        .file(mustExist = false, canBeDir = false, mustBeWritable = true)


    // Mode 2: working with text index

    val textIndexPath: File? by option(help = "Path to JSON file with text index for working with")
        .file(mustExist = true, canBeDir = false, mustBeReadable = true)
    val number: Int? by option(help = "Number of most often met words").int()
    val infoAbout: String? by option(help = "Print info about this word")
    val fromGroup: String? by option(help = "Print info about words from the group")

    // Mode 3: print all lines containing the word

    val printAllLines: String? by option(help = "The word by which all lines containing it will be displayed")

    override fun run() {

        // Mode 1: create text index

        if (output != null && textPath == null) {
            echo("Path to text (--text-path) is required for creating text index")
        }

        if (output != null && textPath != null) {
            try {
                writeTextIndexToJsonFile(
                    getTextIndexFromText(textPath!!.readLines()),
                    output!!
                )
            } catch (e: Exception) {
                echo("Error:")
                echo(e.message)
            }
        }

        // Mode 2: working with text index

        var textIndex: TextIndex? = null
        if (textIndexPath != null) {
            textIndex = getTextIndexFromJson(textIndexPath!!)
        }

        if (number != null && textIndexPath == null) {
            echo("Path to text index in json format (--text-index) is required for working with text index")
        }

        if (number != null && textIndex != null) {
            for (index in mostOftenMetWords(number!!, textIndex)) {
                echo(getWordByIndex(index)?.split(",")?.get(0))
            }
        }

        if (infoAbout != null && textIndex != null) {
            printInfoAboutWord(infoAbout!!, textIndex)
        }

        if (fromGroup != null && textIndex != null) {
            val categoryList = getCategory(fromGroup!!)
            for (word in categoryList) {
                printInfoAboutWord(word, textIndex)
            }
        }

        // Mode 3: print all lines containing the word

        if (textPath == null && printAllLines != null) {
            echo("Path to text (--text-path) is required for --print-all-lines")
        }

        if (printAllLines != null && textPath != null) {
            val textLines = removeEmptyLines(textPath!!.readLines())
            val wordIndex = wordsTrie.getIndexOrNull(printAllLines!!)

            val textIndex = getTextIndexFromText(textPath!!.readLines())
            textIndex.wordsInfo[wordIndex]?.forEach { word ->
                echo(textLines[word.lineNumber])
            }
        }
    }
}

fun main(args: Array<String>) = Input().main(args)

fun printInfoAboutWord(word: String, textIndex: TextIndex) {
    val wordIndex = wordsTrie.getIndexOrNull(word) ?: return
    val wordsList = textIndex.wordsInfo[wordIndex] ?: return

    echo("Word $word was met ${wordsList.size} times")
    for (word in wordsList) {
        echo("On page ${word.pageNumber} in form ${word.wordForm}")
    }
}