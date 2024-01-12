package com.github.darderion.mundaneassignmentpolice.utils.codeDetector

import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.CodeDetectorDataBase.Companion.can_be_the_only_element
import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.CodeDetectorDataBase.Companion.keywords
import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.CodeDetectorDataBase.Companion.delimiters
import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.CodeDetectorDataBase.Companion.can_start_with
import com.github.darderion.mundaneassignmentpolice.utils.codeDetector.Parser.Companion.parseString

class CodeDetector {
    companion object {
        private fun isCodePattern(word: String): Boolean {
            return keywords.contains(word)
        }

        private fun isDelimiter(word: String): Boolean {
            return delimiters.contains(word)
        }

        fun calculateCodeWords(line: String): Double {
            val lineToList = parseString(line)
            var codeWords = lineToList.filter { isCodePattern(it) || isDelimiter(it)}.size
            if (can_be_the_only_element.contains(lineToList[0]) || can_start_with.contains(lineToList[0])) return 1.0
            for (word in lineToList) {
                if (word.toDoubleOrNull() != null) codeWords++
            }

            return codeWords.toDouble()
        }


        fun isLikelyCode(line: String): Boolean {
            if (line.isEmpty() || line.isBlank()) return false
            val lineToList = parseString(line)
            val codeWords = calculateCodeWords(line)
            val totalWords = lineToList.size.toDouble()
            return CodeDetectorLineEvaluation(lineToList, totalWords, codeWords).makeDecision()
        }
    }

}