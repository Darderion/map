package com.github.darderion.mundaneassignmentpolice.checker.rule.word
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

class TwoIdenticalWordsRule(
    word: String,
    private val ignoredIndexes: MutableList<Int>,
    private val neighborhoodSize: Int,
    private val numberOfNeighbors: Int,
    type: RuleViolationType,
    area: PDFRegion,
    name: String,

    ) : WordRule(word, type, area, name) {
    override fun isViolated(document: PDFDocument, line: Int, index: Int): Boolean {
        if (!ignoredIndexes.contains(index)) {
            val wordIndex = index + splitToWordsAndPunctuations(
                document.getTextFromLines(line - neighborhoodSize, line - 1, area)
            ).size +
                    2 * line.coerceAtMost(1).coerceAtMost(neighborhoodSize)
            val words = splitToWordsAndPunctuations(
                document.getTextFromLines(line - neighborhoodSize, line + neighborhoodSize, area)
            )
            val sideWords = mutableListOf(
                words.slice(IntRange(0, wordIndex - 1)).reversed(),
                words.slice(IntRange(wordIndex + 1, words.size - 1))
            )
            val neighbors =  sideWords
                .filter { it.isNotEmpty() }
                .map { it.slice(IntRange(0, numberOfNeighbors - 1)) }
                .flatten()

            if(neighbors[0]==neighbors[1]&&neighbors[0].first().isLetter())
            {
            return true
            }

        }
        return false
    }
}