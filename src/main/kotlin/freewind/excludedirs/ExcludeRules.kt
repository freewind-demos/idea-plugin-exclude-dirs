package freewind.excludedirs

import java.nio.file.FileSystems
import java.nio.file.Paths


data class Rule(private val pattern: String) {
    private val pathMatcher = FileSystems.getDefault().getPathMatcher("glob:$pattern")

    fun matches(relativeFilePath: String): Boolean {
        return pathMatcher.matches(Paths.get(relativeFilePath))
    }

//    private fun standardizePath(path: String): String {
//        fun fixLeadingSlash(p: String) = if (p.startsWith("/")) p else "/$p"
//        fun fixEndingSlash(p: String) = if (p.endsWith("/")) p else "$p/"
//        return path.let(::fixLeadingSlash).let(::fixEndingSlash)
//    }
}

fun parseRules(fileContent: String): List<Rule> {
    return fileContent.lines().map { it.trim() }
            .filter { !it.isEmpty() }
            .filter { !it.startsWith('#') }
            .map { line -> Rule(line) }
}