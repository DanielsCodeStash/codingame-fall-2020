package helper

import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter


// arg 0 - filename
// arg 1 - outfile
// arg 2 - comma separated ignore-list
fun main(args: Array<String>) {

    val inFile = File(args[0])
    val outFile = File(args[1])
    val writer = PrintWriter(outFile)
    writer.print("")

    val lines = inFile.readLines()
    val defaultImports = lines.filter { it.startsWith("import ") }

    val newImports = fixImports(defaultImports, args[2])
    val linesWithoutImports = lines.filter { !it.startsWith("import ") }

    val newFileLines = mutableListOf<String>()
    newFileLines.addAll(newImports)
    newFileLines.addAll(linesWithoutImports)

    FileOutputStream(outFile, true).bufferedWriter().use { out ->
        newFileLines.forEach { out.appendln(it)  }
    }
}

fun fixImports(defaultImports: List<String>, ignores: String): List<String> {

    val splitIgnores = ignores.split(",")

    return defaultImports
            .distinct()
            .filter {import -> !splitIgnores.any {ignore -> import.startsWith("import $ignore") } }
            .toList()
}

