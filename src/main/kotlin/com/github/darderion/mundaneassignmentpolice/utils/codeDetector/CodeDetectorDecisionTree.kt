package com.github.darderion.mundaneassignmentpolice.utils.codeDetector

class CodeDetectorDecisionTree(
    private val lineAsList: List<String>,
    private val totalWords: Int,
    private val codeWords: Int
) {
    private val codeFrequency = codeWords / totalWords


    private val not_only_nonkw = codeWords != 0

    private val can_be_the_only = lineAsList.size == 1 && CodeDetectorDB.can_be_the_only_element.contains(lineAsList[0])

    private val starts_with_CanStartWith = CodeDetectorDB.can_start_with.contains(lineAsList[0])

    private val starts_with_kw = CodeDetectorDB.codePatterns.contains(lineAsList[0])

    private val only_kws = codeWords == totalWords

    private val kws_and_delims_present =
        lineAsList.any() { CodeDetectorDB.codePatterns.contains(it) || CodeDetectorDB.delimiters.contains(it) }
    private val nums_and_delims_present =
        lineAsList.any() { it.toDoubleOrNull() != null || CodeDetectorDB.delimiters.contains(it) }

    private val kw_nonkw_delim =
        (CodeDetectorDB.codePatterns.contains(lineAsList[0]) && (!CodeDetectorDB.codePatterns.contains(lineAsList[1]) ||
                !CodeDetectorDB.delimiters.contains(lineAsList[1])) && CodeDetectorDB.delimiters.contains(lineAsList[2]))

    // OR
    private val two_kws_nonkw_delim =
        (CodeDetectorDB.codePatterns.contains(lineAsList[0]) && (!CodeDetectorDB.codePatterns.contains(lineAsList[1]) ||
                !CodeDetectorDB.delimiters.contains(lineAsList[1]))
                && (!CodeDetectorDB.codePatterns.contains(lineAsList[2]) ||
                !CodeDetectorDB.delimiters.contains(lineAsList[2]))
                && CodeDetectorDB.delimiters.contains(lineAsList[3]))
    private val kw_nonkws_delim =
        (lineAsList.size > 3 && CodeDetectorDB.codePatterns.contains(lineAsList[0]) &&
                (lineAsList.any() { CodeDetectorDB.delimiters.contains(it) }) &&
                ((lineAsList.any() { !CodeDetectorDB.codePatterns.contains(it) }) && (lineAsList.any() {
                    !CodeDetectorDB.delimiters.contains(
                        it
                    )
                })))

    private val delim_is_the_last = CodeDetectorDB.delimiters.contains(lineAsList.last())

    // starts with nonkw
    private val nonkw_delim =
        (!CodeDetectorDB.codePatterns.contains(lineAsList[0]) && !CodeDetectorDB.delimiters.contains(lineAsList[0]) && CodeDetectorDB.delimiters.contains(
            lineAsList[1]
        ))

    // OR
    private val two_nonkws_delim =
        (!CodeDetectorDB.codePatterns.contains(lineAsList[0]) && !CodeDetectorDB.delimiters.contains(lineAsList[0]) &&
                !CodeDetectorDB.codePatterns.contains(lineAsList[1]) && !CodeDetectorDB.delimiters.contains(lineAsList[1]) &&
                CodeDetectorDB.delimiters.contains(lineAsList[2]))

    private val dot_notation = findDotNotation(lineAsList)
    private val function_call = findFunctionCall(lineAsList)
    private val codeFrequency_greater_than_threshold = codeFrequency >= FREQUENCY_THRESHOLD

    private fun findDotNotation(line: List<String>): Boolean {
        if ("." !in line) return false
        val size = line.size

        TODO()

    }

    private fun findFunctionCall(line: List<String>) {
        TODO()
    }

    internal val properties = listOf(
        // if false -> not a code line
        not_only_nonkw,
        // must be a code line
        can_be_the_only,
        starts_with_CanStartWith,
        //--------------------
        starts_with_kw,
        only_kws,
        kws_and_delims_present,
        nums_and_delims_present,
        kw_nonkw_delim,
        two_kws_nonkw_delim,
        kw_nonkws_delim,
        delim_is_the_last,
        nonkw_delim,
        two_nonkws_delim,
        dot_notation,
        function_call,
        codeFrequency_greater_than_threshold
    )

    fun makeDecision(): Boolean {

        TODO()

    }


    companion object {
        private val FREQUENCY_THRESHOLD = Threshold.FREQUENCY_THRESHOLD

        /**
         * TOTAL number of facts = 9 => if FACTS_TRUE/TOTAL >= 0.5 -> is likely a code line;
         *
         * 0.5 is the probability of a line being a code line or not
         */
        private const val INITIAL_POSSIBILITY = 0.5
        private const val TOTAL = 15.0
    }
}