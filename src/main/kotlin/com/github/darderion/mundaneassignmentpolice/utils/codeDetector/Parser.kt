package com.github.darderion.mundaneassignmentpolice.utils.codeDetector


class Parser {
    companion object {
        private fun isDelim(letter: String): Boolean {
            return (CodeDetectorDataBase.delimiters.contains(letter) || CodeDetectorDataBase.can_start_with.contains(
                letter
            ) || CodeDetectorDataBase.can_be_the_only_element.contains(letter))
        }

        internal fun parseString(lineInput: String): List<String> {
            val line = lineInput.trim()
            val parseResult: MutableList<String> = mutableListOf()
            var word = ""
            var delimiter = ""
            var index = 0

            for (s in line) {
                if (index == line.indices.last) {
                    if (isDelim(s.toString())) {
                        if (word.isNotEmpty()) parseResult.add(word)
                        if (delimiter.isNotEmpty() && isDelim(delimiter.plus(s.toString()))) parseResult.add(
                            delimiter.plus(
                                s.toString()
                            )
                        )
                        else {
                            if (delimiter.isEmpty()) parseResult.add(s.toString())
                            else parseResult.add(delimiter)

                        }
                        continue
                    } else {
                        if (word.isNotEmpty()) {
                            word = word.plus(s.toString())
                            parseResult.add(word)
                        }
                        else if (!s.isWhitespace()) parseResult.add(s.toString())
                        continue
                    }
                }
                if (isDelim(s.toString())) {
                    if (word.isNotEmpty()) {
                        parseResult.add(word)
                        word = ""
                    }
                    if (isDelim(delimiter.plus(s.toString()))) {
                        delimiter = delimiter.plus(s.toString())
                    } else {
                        if (delimiter.isNotEmpty()) {
                            parseResult.add(delimiter)
                            delimiter = s.toString()
                        }
                    }
                }
                if (s.isWhitespace()) {
                    if (word.isNotEmpty()) {
                        parseResult.add(word)
                        word = ""
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
                }

                index++
            }
//            println(parseResult.toList()) // can be useful for debugging
            return parseResult
        }
    }
}