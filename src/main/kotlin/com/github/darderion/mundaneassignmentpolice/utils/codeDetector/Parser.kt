package com.github.darderion.mundaneassignmentpolice.utils.codeDetector


class Parser {
    companion object {
        private fun isDelim(letter: String): Boolean {
            return (CodeDetectorDataBase.delimiters.contains(letter) || CodeDetectorDataBase.can_start_with.contains(
                letter
            ) || CodeDetectorDataBase.can_be_the_only_element.contains(letter))
        }

        internal fun parseString(line: String): List<String> {
            val parseResult: MutableList<String> = mutableListOf()
            var word = ""
            var delimiter = ""
            var index = 0

            for (s in line) {
                if (index == line.indices.last) {
                    if (isDelim(s.toString())) {
                        if (delimiter.isNotEmpty() && isDelim(delimiter.plus(s.toString()))) parseResult.add(delimiter.plus(s.toString()))
                        else {
                            parseResult.add(delimiter)
                            parseResult.add(s.toString())
                        }
                    }
                    if (word.isNotEmpty()) parseResult.add(word)
                    continue
                }
                if (isDelim(s.toString())) {
                    if (word.isNotEmpty()) {
                        parseResult.add(word)
                        word = ""
                    }
                    if (isDelim(delimiter.plus(s.toString()))) {
                        delimiter = delimiter.plus(s.toString())
//                        continue
                    } else {
                        if (delimiter.isNotEmpty()) {
                            parseResult.add(delimiter)
                            delimiter = s.toString()
                        }
                    }
//                    if (s.isWhitespace() && delimiter.isNotEmpty()) {
//                        parseResult.add(delimiter)
//                        delimiter = s.toString()
//                    }
                }
                if (s.isWhitespace()) {
                    if (word.isNotEmpty()) {
                        parseResult.add(word)
                        word = ""
//                        continue
                    }
                    if (delimiter.isNotEmpty()) {
                        parseResult.add(delimiter)
                        delimiter = ""
                    }
                }
                if (!isDelim(s.toString()) && !s.isWhitespace()) {
                    word = word.plus(s.toString())
                    if (delimiter.isNotEmpty()) {
                        parseResult.add(delimiter)
                        delimiter = ""
                    }
//                    continue
                }

                index++
            }
//            println(parseResult)
            return parseResult
        }
    }
}