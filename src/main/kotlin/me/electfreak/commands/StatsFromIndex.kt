package me.electfreak.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import me.electfreak.*
import java.io.File

class StatsFromIndex : CliktCommand() {
    private val textIndexPath: File by option(help = "Path to JSON file with text index for working with")
        .file(mustExist = true, canBeDir = false, mustBeReadable = true).required()
    private val number: Int? by option(help = "Number of most often met words").int()
    private val word: String? by option(help = "Print info about this word")
    private val fromGroup: String? by option(help = "Print info about words from the group")

    override fun run() {
        try {
            val textIndex = getTextIndexFromJson(textIndexPath) ?: printError("Failed to parse text index")

            word?.let { printInfoAboutWord(it, textIndex) }

            fromGroup?.let {
                val categoryList = getCategory(it)
                for (word in categoryList) {
                    printInfoAboutWord(word, textIndex)
                }
            }

            number?.let {
                echo("Top $number words:")
                for (wordIndex in getMostOftenMetWords(it, textIndex)) {
                    echo(getWordByIndex(wordIndex))
                }
            }

        } catch (e: Exception) {
            echo("Failed to work with text index: ")
            echo(e.message)
        }
    }

    private fun printInfoAboutWord(word: String, textIndex: TextIndex) {
        val wordIndex = wordsTrie.getIndexOrNull(word) ?: return
        val wordsList = textIndex.wordsInfo[wordIndex] ?: return

        echo("Word $word was met ${wordsList.size} times")
        for (wordOccurrence in wordsList) {
            echo("On page ${wordOccurrence.pageNumber} in form ${wordOccurrence.wordForm}")
        }
    }
}