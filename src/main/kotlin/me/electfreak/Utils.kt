package me.electfreak

import com.github.ajalt.clikt.output.TermUi
import kotlin.system.exitProcess


val wordsTrie = makeWordsTrie(pathToDictionary)

fun printError(message: String): Nothing {
    TermUi.echo(message, err = true)
    exitProcess(1)
}