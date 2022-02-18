package com.github.darderion.mundaneassignmentpolice.checker.rule.symbol

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.checker.Direction.*
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion


/**
 * Rule that looks for closest symbol that is not IGNORED
 * 		If that symbol is DISALLOWED then a rule violation is detected
 * 		If REQUIRED_SYMBOLS is not empty and that symbol is not REQUIRED then a rule violation is detected
 */
class BasicSymbolRule(
	symbol: Char,
	private val ignoredNeighbors: MutableList<Char>,
	private val ignoredIndexes: MutableList<Int>,
	private val disallowedNeighbors: MutableList<Char>,
	private val requiredNeighbors: MutableList<Char>,
	private val direction: Direction,
	private val neighborhoodSize: Int,
	area: PDFRegion,
	name: String
): SymbolRule(symbol, area, name) {
	override fun isViolated(document: PDFDocument, line: Int, index: Int): Boolean {
		if (!ignoredIndexes.contains(index)) {
			val symbolIndex = index + document.getTextFromLines(line - neighborhoodSize, line - 1, area).length +
					2 * neighborhoodSize.coerceAtMost(line)
			val text = document.getTextFromLines(line - neighborhoodSize, line + neighborhoodSize, area)

			val sideTexts = mutableListOf(
				text.slice(IntRange(0, symbolIndex - 1)).reversed(),
				text.slice(IntRange(symbolIndex + 1, text.length - 1))
			)

			when (direction) {
				LEFT -> sideTexts.removeAt(1)
				RIGHT -> sideTexts.removeAt(0)
			}

			val neighbors = sideTexts
				.map { it.filterNot { ignoredNeighbors.contains(it) } }		// Remove ignored symbols
				.filter { it.isNotEmpty() }                                 // Remove empty lines
				.map { it.first() }

			if (neighbors.any { disallowedNeighbors.contains(it) } ||
				(requiredNeighbors.isNotEmpty() && neighbors.any { !requiredNeighbors.contains(it) })) {
				return true
			}
		}
		return false
	}
}
