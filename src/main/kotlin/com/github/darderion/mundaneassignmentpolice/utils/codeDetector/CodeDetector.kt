package com.github.darderion.mundaneassignmentpolice.utils.codeDetector

import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.CodeDetectorDB.Companion.codePatterns
import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.CodeDetectorDB.Companion.delimiters
import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.CodeDetectorDB.Companion.can_start_with
import java.lang.Double.parseDouble

class CodeDetector {
    companion object {
        private fun isCodePattern(word: String): Boolean {
            return word in codePatterns
        }

        private fun isDelimiter(word: String): Boolean {
            return word in delimiters
        }

        internal fun splitIntoList(line: String): List<String> { // splits a line of text into words by delimiters and stores them into list
            val splitList: MutableList<String> = mutableListOf()
            var word = ""

            for (l in line) {
                if (!isDelimiter(l.toString()) && !l.isWhitespace()) {
                    word = word.plus(l.toString())

                    if (l == line.last()) {
                        splitList.add(word)
                    }
                    continue
                }
                if (isDelimiter(l.toString()) && l == line.last()) {
                    if (word != "") splitList.add(word)
                    splitList.add(l.toString())
                    continue
                }
                if (word != "" && l == line.last()) {
                    splitList.add(word)
                    continue
                }
                if (l.isWhitespace()) {
                    if (word != "") splitList.add(word)
                    if (isDelimiter(l.toString())) splitList.add(l.toString())
                    word = ""
                    continue
                }
                if (isDelimiter(l.toString())) {
                    if (word != "") splitList.add(word)
                    splitList.add(l.toString())
                    word = ""
                }
            }
            return splitList
        }



        fun isLikelyCode(line: String): Boolean {
            val lineToList = splitIntoList(line)
            val totalWords = lineToList.size
            var codeWords = lineToList.filter { codePatterns.contains(it) || delimiters.contains(it) }.size
            if (line.startsWith("    ") || line.startsWith("\t")) codeWords++
            for (word in lineToList) {
                try {
                    parseDouble(word)
                    codeWords++
                } catch (e: NumberFormatException) {
                    continue
                }
            }
            if (lineToList[0] in can_start_with) {
                return true
            }
            if (lineToList[0] !in codePatterns && lineToList[0] !in can_start_with) {
                codeWords--
            }
            if (!codePatterns.contains(lineToList[0]) && !delimiters.contains(lineToList[0])) codeWords--

            return codeWords >= totalWords - codeWords
        }
    }

}