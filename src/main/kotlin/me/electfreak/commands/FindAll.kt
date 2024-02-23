package me.electfreak.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import me.electfreak.*
import java.io.File

class FindAll : CliktCommand() {
    private val textPath: File by option(help = "Path to txt file")
        .file(mustBeReadable = true).required()
    private val textIndexPath: File? by option(help = "Path to JSON file with text index for working with")
        .file(mustExist = true, canBeDir = false, mustBeReadable = true)
    private val word: String by option(help = "The word by which all lines containing it will be displayed").required()

    override fun run() {
        val textIndex = textIndexPath?.let { getTextIndexFromJson(it) } ?: getTextIndexFromText(textPath.readLines())
        val textLines = removeEmptyLines(textPath.readLines())
        val wordIndex = wordsTrie.getIndexOrNull(word) ?: printError("Unknown word")

        try {
            textIndex.wordsInfo[wordIndex]?.map { word ->
                word.lineNumber
            }?.toSet()?.forEach {
                echo(textLines[it])
            }

        } catch (e: Exception) {
            echo("Failed to print all lines: \n${e.message}")
        }
    }
}