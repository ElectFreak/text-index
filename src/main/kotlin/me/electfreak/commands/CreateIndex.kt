package me.electfreak.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.file
import me.electfreak.getTextIndexFromText
import me.electfreak.writeTextIndexToJsonFile
import java.io.File

class CreateIndex : CliktCommand() {
    private val textPath: File by option(help = "Path to txt file")
        .file(mustBeReadable = true).required()
    private val outputPath: File by option(help = "Path to JSON file for writing text index")
        .file().required().validate { !it.exists() || it.canWrite() }

    override fun run() {
        try {
            writeTextIndexToJsonFile(
                getTextIndexFromText(textPath.readLines()),
                outputPath
            )
        } catch (e: Exception) {
            echo("Failed to write text index:")
            echo(e.message)
        }
    }
}