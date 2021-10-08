package com.github.darderion.mundaneassignmentpolice.checker.rule

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.checker.Direction.*
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument

/**
 * Rule that looks for closest symbol that is not IGNORED
 * 		If that symbol is DISALLOWED then a rule violation is detected
 * 		If REQUIRED_SYMBOLS is not empty and that symbol is not REQUIRED then a rule violation is detected
 */
class SymbolRule(
	private val symbol: Char,
	private val ignoredNeighbors: MutableList<Char>,
	private val disallowedNeighbors: MutableList<Char>,
	private val requiredNeighbors: MutableList<Char>,
	private val direction: Direction,
	private val neighborhoodSize: Int,
	private val name: String
) {
	fun process(document: PDFDocument): List<RuleViolation> {
		val rulesViolations: MutableList<RuleViolation> = mutableListOf()

		document.text.forEachIndexed { index, pdfText ->
			pdfText.content.indicesOf(symbol.toString()).forEach {
				val symbolIndex = it + document.getTextFromLines(index - neighborhoodSize, index - 1).length +
						2*neighborhoodSize.coerceAtMost(index)
				val text = document.getTextFromLines(index - neighborhoodSize, index + neighborhoodSize)

				val sideTexts = mutableListOf(
					text.slice(IntRange(0, symbolIndex - 1)).reversed(),
					text.slice(IntRange(symbolIndex + 1, text.length - 1))
				)

				when(direction) {
					LEFT -> sideTexts.removeAt(1)
					RIGHT -> sideTexts.removeAt(0)
				}

				val neighbors = sideTexts
					.map { it.filterNot { ignoredNeighbors.contains(it) } }	// Remove ignored symbols
					.filter { it.isNotEmpty() }								// Remove empty lines
					.map { it.first() }

				if (neighbors.any { disallowedNeighbors.contains(it) } ||
					(requiredNeighbors.isNotEmpty() && neighbors.any { !requiredNeighbors.contains(it) })) {
					rulesViolations.add(RuleViolation(pdfText.line, pdfText.page, name))
				}
			}
		}

		return rulesViolations
	}
}
