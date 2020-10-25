// Думай позитивно: стакан всегда наполовину полон, всегда.
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import java.io.File
import java.lang.Exception


val wordsTrie = makeWordsTrie(pathToDictionary)

class Input : CliktCommand() {

    // Mode 1: create text index

    val textPath: File? by option(help = "Path to txt file")
        .file()
    val output: File? by option(help = "Path to JSON file for writing text index")
        .file()


    // Mode 2: working with text index

    val textIndexPath: File? by option(help = "Path to JSON file with text index for working with")
        .file(mustExist = true, canBeDir = false, mustBeReadable = true)
    val number: Int? by option(help = "Number of most often met words").int()
    val infoAbout: String? by option(help = "Print info about this word")
    val fromGroup: String? by option(help = "Print info about words from the group")

    // Mode 3: print all lines containing the word

    val printAllLines: String? by option(help = "The word by which all lines containing it will be displayed")

    override fun run() {

        if (textPath != null && textPath!!.canRead()) {
            // Task 1: write text index to file
            if (output != null && (output!!.canWrite() || !output!!.exists())) {
                try {
                    writeTextIndexToJsonFile(
                        getTextIndexFromText(textPath!!.readLines()),
                        output!!
                    )
                } catch (e: Exception) {
                    echo("Failed to write text index:")
                    echo(e.message)
                }
            }

            // Task 3: print all lines containing the word
            if (printAllLines != null) {
                val textLines = removeEmptyLines(textPath!!.readLines())
                val wordIndex = wordsTrie.getIndexOrNull(printAllLines!!)
                try {
                    val textIndex = getTextIndexFromText(textPath!!.readLines())
                    textIndex.wordsInfo[wordIndex]?.forEach { word ->
                        echo(textLines[word.lineNumber])
                    }

                } catch (e: Exception) {
                    echo("Failed to print all lines: ")
                    echo(e.message)
                }
            }

        }

        // Task 2: working with existing text index file
        if (textIndexPath != null && textIndexPath!!.canRead()) {
            try {
                val textIndex = getTextIndexFromJson(textIndexPath!!)

                if (infoAbout != null && textIndex != null) {
                    printInfoAboutWord(infoAbout!!, textIndex)
                }

                if (fromGroup != null && textIndex != null) {
                    val categoryList = getCategory(fromGroup!!)
                    for (word in categoryList) {
                        printInfoAboutWord(word, textIndex)
                    }
                }

                if (number != null && textIndex != null) {
                    for (wordIndex in getMostOftenMetWords(number!!, textIndex)) {
                        echo(getWordByIndex(wordIndex))
                    }
                }

            } catch (e: Exception) {
                echo("Failed to work with text index: ")
                echo(e.message)
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