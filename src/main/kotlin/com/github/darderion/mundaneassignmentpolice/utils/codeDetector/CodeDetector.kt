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

//        internal fun parseString(line: String): List<String> {
//            val parseResult: MutableList<String> = mutableListOf()
//            var word = ""
//
//            for (l in line) { // TODO: check the parser
//                if (!isDelimiter(l.toString()) && !l.isWhitespace()) {
//                    word = word.plus(l.toString())
//
//                    if (l == line.last()) {
//                        parseResult.add(word)
//                    }
//                    continue
//                }
//                if (isDelimiter(l.toString()) && l == line.last()) {
//                    if (word != "") parseResult.add(word)
//                    parseResult.add(l.toString())
//                    continue
//                }
//                if (word != "" && l == line.last()) {
//                    parseResult.add(word)
//                    continue
//                }
//                if (l.isWhitespace()) {
//                    if (word != "") parseResult.add(word)
//                    if (isDelimiter(l.toString())) parseResult.add(l.toString())
//                    word = ""
//                    continue
//                }
//                if (isDelimiter(l.toString())) {
//                    if (word != "") parseResult.add(word)
//                    parseResult.add(l.toString())
//                    word = ""
//                }
//            }
//            return parseResult
//        }

        fun calculateCodeWords(line: String): Double {
            val lineToList = parseString(line)
            var codeWords = lineToList.filter { isCodePattern(it) || isDelimiter(it)}.size
            if (can_be_the_only_element.contains(lineToList[0]) || can_start_with.contains(lineToList[0])) return 1.0
//            if (line.startsWith("    ") || line.startsWith("\t")) codeWords++
            for (word in lineToList) {
                if (word.toDoubleOrNull() != null) codeWords++
            }

            return codeWords.toDouble()
        }


        fun isLikelyCode(line: String): Boolean {
            val lineToList = parseString(line)
            if (lineToList[0] in can_start_with) {
                return true
            }
            val codeWords = calculateCodeWords(line)
            val totalWords = lineToList.size.toDouble()
            return CodeDetectorLineEvaluation(lineToList, totalWords, codeWords).makeDecision()
        }
    }

}