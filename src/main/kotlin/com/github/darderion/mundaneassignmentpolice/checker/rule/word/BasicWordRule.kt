package com.github.darderion.mundaneassignmentpolice.checker.rule.word

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

class BasicWordRule(
	word: String,
	private val ignoredNeighbors: MutableList<Regex>,
	private val notIgnoredNeighbors: MutableList<Regex>,
	private val ignoredIndexes: MutableList<Int>,
	private val isPunctuationIgnored: Boolean,
	private val areWordsIgnored: Boolean,
	private val disallowedNeighbors: MutableList<Regex>,
	private val requiredNeighbors: MutableList<Regex>,
	private val direction: Direction,
	private val neighborhoodSize: Int,
	private val numberOfNeighbors: Int,
	override val ruleBody: (neighbors: List<String>, requiredNeighbors: MutableList<Regex>, disallowedNeighbors: MutableList<Regex>) -> Boolean,
	type: RuleViolationType,
	area: PDFRegion,
	name: String
) : WordRule(word, ruleBody , type, area, name) {
	override fun isViolated(document: PDFDocument, line: Int, index: Int, ruleBody :(neighbors:List<String>, requiredNeighbors: MutableList<Regex>, disallowedNeighbors: MutableList<Regex>) -> Boolean): Boolean {
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

			when (direction) {
				Direction.LEFT -> sideWords.removeAt(1)
				Direction.RIGHT -> sideWords.removeAt(0)
			}

			val filteredSideWords = sideWords
				.map { it.filter { !isPunctuationIgnored || it.first().isLetterOrDigit() } }
				.map { it.filter { !areWordsIgnored || !it.first().isLetterOrDigit() } }

			val neighbors = (if (notIgnoredNeighbors.isNotEmpty()) filteredSideWords
				.map { it.filter { word -> notIgnoredNeighbors.any { regex -> regex.matches(word) } } }    // Keep only non-ignored symbols
			else filteredSideWords
				.map { it.filterNot { word -> ignoredNeighbors.any { regex -> regex.matches(word) } } })    // Remove ignored symbols
				.filter { it.isNotEmpty() }                                // Remove empty lines
				.map { it.slice(IntRange(0, numberOfNeighbors - 1)) }
				.flatten()
			return ruleBody(neighbors,requiredNeighbors,disallowedNeighbors)
		}

		return false
	}
}