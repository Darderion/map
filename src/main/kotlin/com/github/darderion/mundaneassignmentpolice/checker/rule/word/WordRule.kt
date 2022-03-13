package com.github.darderion.mundaneassignmentpolice.checker.rule.word

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside

class WordRule(
	val word: String,
	private val ignoredNeighbors: MutableList<String>,
	private val notIgnoredNeighbors: MutableList<String>,
	private val ignoredIndexes: MutableList<Int>,
	private val isPunctuationIgnored: Boolean,
	private val areWordsIgnored: Boolean,
	private val disallowedNeighbors: MutableList<String>,
	private val requiredNeighbors: MutableList<String>,
	private val direction: Direction,
	private val neighborhoodSize: Int,
	type: RuleViolationType,
	area: PDFRegion,
	name: String
) : Rule(area, name, type) {
	fun isViolated(document: PDFDocument, line: Int, index: Int): Boolean {
		if (!ignoredIndexes.contains(index)) {
			val wordIndex = index + document.getTextFromLines(line - neighborhoodSize, line - 1, area)
				.split(' ').map { splitToWordsAndPunctuations(it) }.flatten().size +
					line.coerceAtMost(1).coerceAtMost(neighborhoodSize)
			val words = document.getTextFromLines(line - neighborhoodSize, line + neighborhoodSize, area)
				.split(' ').map { splitToWordsAndPunctuations(it) }.flatten()

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
				.map { it.filter { notIgnoredNeighbors.contains(it) } }    // Keep only non-ignored symbols
			else filteredSideWords
				.map { it.filterNot { ignoredNeighbors.contains(it) } })    // Remove ignored symbols
				.filter { it.isNotEmpty() }                                // Remove empty lines
				.map { it.first() }

			if (neighbors.any { disallowedNeighbors.contains(it) } ||
				(requiredNeighbors.isNotEmpty() && (neighbors.isEmpty() ||
						neighbors.any { !requiredNeighbors.contains(it) }))) {
				return true
			}
		}
		return false
	}

	override fun process(document: PDFDocument): List<RuleViolation> {
		val rulesViolations: MutableList<RuleViolation> = mutableListOf()

		document.text.filter { it.area!! inside area }.forEachIndexed { lineIndex, line ->
			line.content.split(' ').map { splitToWordsAndPunctuations(it) }.flatten()
				.mapIndexed { wordIndex, wordText -> if (wordText == this.word) wordIndex else -1 }
				.filter { it != -1 }.forEach {
					if (isViolated(document, lineIndex, it)) {
						rulesViolations.add(RuleViolation(listOf(line), name, type))
					}
				}
		}

		return rulesViolations
	}

	fun splitToWordsAndPunctuations(str: String): List<String> {
		val wordsAndPunctuations: MutableList<String> = mutableListOf("")
		var index = 0
		for (symbol in str) {
			if (symbol.isLetterOrDigit()){
				wordsAndPunctuations[index] += symbol.toString()
				continue
			}
			wordsAndPunctuations.addAll(listOf(symbol.toString(), ""))
			index += 2
		}

		return wordsAndPunctuations.filter { it != "" }
	}
}