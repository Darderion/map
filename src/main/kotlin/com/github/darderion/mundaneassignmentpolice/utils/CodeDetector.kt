package com.github.darderion.mundaneassignmentpolice.utils


class CodeDetector {


    companion object {

        private fun isCodePattern(word: String): Boolean {
            return word in codePatterns
        }

        private fun isDelimiter(word: String): Boolean {
            return word in delimiters
        }

        private fun splitIntoList(line: String): List<String> { // splits a line of text into words by delimiters and stores them into list
            val splitList: MutableList<String> = mutableListOf()
            var word = ""
            var quote = 0
            for (l in line) {
                if (!isDelimiter(l.toString()) && !l.isWhitespace()) {
                    if (l == '"') quote++
                    if (isCodePattern(word)) {
                        splitList.add(word)
                        word = ""
                        continue
                    }
                    word = word.plus(l.toString())
                } else {
                    if (quote == 1) continue
                    if (quote == 2) quote = 0
                    if (l.isWhitespace() && word != "") {
                        splitList.add(word)
                        word = ""
                        continue
                    }
                    if (word != "") splitList.add(word)
                    if (isDelimiter(l.toString())) splitList.add(l.toString())
                    word = ""
                }
            }
            return splitList
        }

        fun isLikelyCode(line: String): Boolean {
            val lineToList = splitIntoList(line)
            val codeFrequency: Double
            val totalWords = lineToList.size
            var codeWords = lineToList.filter { codePatterns.contains(it) || delimiters.contains(it) }.size
            if (line.startsWith("    ") || line.startsWith("\t")) codeWords++
            codeFrequency = codeWords.toDouble() / totalWords.toDouble()
            if (codeFrequency >= THRESHOLD) {
                return true
            }
            return false
        }

        private val codePatterns = listOf(
            // Common
            "+",
            "-",

            // C language
            "int",
            "char",
            "void",
            "char*",
            "int*",
            "if",
            "else",
            "for",
            "while",
            "struct",
            "return",
            "include",
            "define",
            "pragma",
            "float",
            "double",
            "float*",
            "double*",
            "long",
            "short",
            "switch",
            "case",

            // Java language
            "abstract",
            "assert",
            "boolean",
            "break",
            "byte",
            "catch",
            "char",
            "class",
            "continue",
            "default",
            "do",
            "enum",
            "extends",
            "final",
            "finally",
            "implements",
            "import",
            "instanceof",
            "native",
            "new",
            "package",
            "private",
            "public",
            "static",
            "super",
            "synchronized",
            "this",
            "throw",
            "throws",
            "transient",
            "try",
            "volatile",
            "null",
            "true",
            "false",
            "var",
            "val",
            "open",
            "opens",
            "module",
            "exports",
            "non-sealed",
            "permits",
            "provides",
            "record",
            "requires",
            "sealed",
            "to",
            "transitive",
            "uses",
            "and",
            "with"
        )

        private val delimiters = listOf(";", "{", "}", "[", "]", "(", ")", ":", "*", "#", "!", "?", "//", "!!")

        private const val THRESHOLD = 0.1
    }

}