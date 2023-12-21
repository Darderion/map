package com.github.darderion.mundaneassignmentpolice.utils.codeDetector

class CodeDetectorLineEvaluation(
    lineAsList: List<String>,
    totalWords: Double,
    codeWords: Double
) {
    private val not_only_nonKW = codeWords != 0.0 // line consists not only of non-keywords

    private val can_be_the_only =
        lineAsList.size == 1 && CodeDetectorDataBase.can_be_the_only_element.contains(lineAsList[0])

    private val starts_with_CanStartWith =
        lineAsList.isNotEmpty() && CodeDetectorDataBase.can_start_with.contains(lineAsList[0])

    private val assignment_present = lineAsList.any() { it == "=" }

    private val assignment_or_colon_size3 =
        lineAsList.size >= 2 && (lineAsList[1] == "=" || lineAsList[1] == ":") // '=' or ':' in specific position

    private val assignment_or_colon_size4 = lineAsList.size >= 3 && (lineAsList[2] == "=" || lineAsList[2] == ":")


    private val starts_with_kw = isKW(lineAsList[0])

    private val only_kws = codeWords == totalWords // line consists only of keywords

    private val kws_and_delims_present =
        lineAsList.any { isKW(it) || isDelim(it) }

    private val nums_and_delims_present =
        lineAsList.any { it.toDoubleOrNull() != null || isDelim(it) }

    private val kw_nonKW_delim = // Example: "int something = 0;"
        if (lineAsList.size >= 3)
            (isKW(lineAsList[0]) && isNonKW(lineAsList[1]) && isDelim(lineAsList[2]))
        else false

    // OR
    private val two_kws_nonKW_delim = // Example: "private val something = "something" "
        if (lineAsList.size >= 4)
            (isKW(lineAsList[0]) && isKW(lineAsList[1]) && isNonKW(lineAsList[2]) && isDelim(lineAsList[3]))
        else false


    private val kw_nonKWs_delim = // same as previous but for multiple non-KWs
        (lineAsList.size >= 4 && isKW(lineAsList[0]) &&
                (lineAsList.any { isDelim(it) }) &&
                (lineAsList.any { isNonKW(it) }))

    private val delim_is_the_last = isDelim(lineAsList.last())

    // starts with nonKW
    private val nonKW_delim =
        if (lineAsList.size >= 2)
            (isNonKW(lineAsList[0]) && isDelim(lineAsList[1]))
        else false

    // OR
    private val two_nonKWs_delim =
        if (lineAsList.size >= 3)
            (isNonKW(lineAsList[0]) &&
                    isNonKW(lineAsList[1]) &&
                    isDelim(lineAsList[2]))
        else false

    private val dot_notation = findDotNotation(lineAsList)

    private val function_call = findFunctionCall(lineAsList)

    private val operators_present = lineAsList.any() { CodeDetectorDataBase.operators.contains(it) }

    private fun isNonKW(word: String): Boolean {
        return (!CodeDetectorDataBase.keywords.contains(word) && !CodeDetectorDataBase.delimiters.contains(word) && word.toDoubleOrNull() == null)
    }

    private fun isKW(word: String): Boolean {
        return (CodeDetectorDataBase.keywords.contains(word))
    }

    private fun isDelim(word: String): Boolean {
        return (CodeDetectorDataBase.delimiters.contains(word))
    }

    private fun findDotNotation(line: List<String>): Boolean {
        if ("." !in line) return false
        var functionCallStarted = 0
        var functionCallFinished = 0
        var dotCallStarted = 0
        var prevWord = ""

        if (line.size >= 2 && line[0] == "." && (isKW(line[1]) || isNonKW(line[1]))) return true
        else {
            if (line.size >= 2) {
                for (word in line) {
                    if (isKW(word) || isNonKW(word)) {
                        if ((dotCallStarted == 1) && (prevWord == ".")) {
                            prevWord = word
                            continue
                        } else {
                            prevWord = word
                            continue
                        }
                    }
                    if ((word == "." && (isKW(prevWord) || isNonKW(prevWord) || (prevWord == ")" && functionCallFinished == 1)))) {
                        prevWord = word
                        dotCallStarted = 1
                        continue
                    }
                    if (word == "(" && (isKW(prevWord) || isNonKW(prevWord))) {
                        functionCallStarted = 1
                        prevWord = "("
                        continue
                    }
                    if ((word == ")" || word == ");") && (prevWord == "(" || (isKW(prevWord) || isNonKW(prevWord))) && functionCallStarted == 1) {
                        if (dotCallStarted == 1) return true
                        functionCallFinished = 1
                        prevWord = ")"
                        continue
                    }
                }
            }
        }
        return false
    }

    private fun findFunctionCall(line: List<String>): Boolean {
        var openBracket = 0
        var prevWord: String
        for (word in line) {
            prevWord = word
            if (word == "(") {
                if (isKW(prevWord) || isNonKW(prevWord)) openBracket = 1
                else continue
            }
            if (word == ")" || word == ");") {
                return openBracket == 1
            }
        }

        return false
    }

    private val properties = listOf(
        // if false -> not a code line
        not_only_nonKW,
        // must be a code line
        assignment_or_colon_size3,
        assignment_or_colon_size4,
        can_be_the_only,
        starts_with_CanStartWith,
        assignment_present,
        only_kws,
        dot_notation,
        function_call,
        // must be compared with PROPERTIES_THRESHOLD
        operators_present,
        starts_with_kw,
        kws_and_delims_present,
        nums_and_delims_present,
        kw_nonKW_delim,
        two_kws_nonKW_delim,
        kw_nonKWs_delim,
        delim_is_the_last,
        nonKW_delim,
        two_nonKWs_delim
    )

    private val PROPERTIES_TOTAL = properties.size.toDouble()
    internal fun calculatePropertiesThreshold(): Double {
        val PROPERTIES_TRUE = properties.filter { it }.size.toDouble()
        val PROPERTIES_PROBABILITY = PROPERTIES_TRUE / PROPERTIES_TOTAL

        return PROPERTIES_PROBABILITY
    }

    private val FREQUENCY_PROBABILITY = codeWords / totalWords
    private val PROPERTIES_PROBABILITY = calculatePropertiesThreshold()

    fun makeDecision(): Boolean {
        if (!not_only_nonKW) return false
        if (starts_with_CanStartWith || assignment_present || only_kws || function_call || dot_notation || assignment_or_colon_size3 || assignment_or_colon_size4) return true
        return ((FREQUENCY_PROBABILITY >= FREQUENCY_THRESHOLD) && (PROPERTIES_PROBABILITY >= PROPERTIES_THRESHOLD))
    }

    companion object {
        private val FREQUENCY_THRESHOLD = Threshold.FREQUENCY_THRESHOLD
        private val PROPERTIES_THRESHOLD = Threshold.PROPERTIES_THRESHOLD
    }
}
