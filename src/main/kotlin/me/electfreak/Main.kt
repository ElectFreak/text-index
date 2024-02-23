// Думай позитивно: стакан всегда наполовину полон, всегда.
package me.electfreak

import com.github.ajalt.clikt.core.subcommands
import me.electfreak.commands.CreateIndex
import me.electfreak.commands.FindAll
import me.electfreak.commands.StatsFromIndex
import me.electfreak.commands.TextIndexCommand

fun main(args: Array<String>) = TextIndexCommand().subcommands(CreateIndex(), FindAll(), StatsFromIndex()).main(args)

