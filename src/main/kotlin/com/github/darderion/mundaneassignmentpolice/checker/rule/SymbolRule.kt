package com.github.darderion.mundaneassignmentpolice.checker.rule

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.checker.Direction.*
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFDocument

/**
 * Rule that looks for closest symbol that is not IGNORED
 * 		If that symbol is DISALLOWED then a rule violation is detected
 * 		If REQUIRED_SYMBOLS is not empty and that symbol is not REQUIRED then a rule violation is detected
 */
class SymbolRule(
	val symbol: Char = ' ',
	val ignoredNeighbors: MutableList<Char> = mutableListOf(),
	val disallowedNeighbors: MutableList<Char> = mutableListOf(),
	var requiredNeighbors: MutableList<Char> = mutableListOf(),
	var direction: Direction = Direction.BIDIRECTIONAL,
	val neighborhoodSize: Int = 1,
	val name: String
) {
	fun process(document: PDFDocument): List<RuleViolation> {
		val rulesViolations: MutableList<RuleViolation> = mutableListOf()

		document.text.drop(1).dropLast(1).forEachIndexed { previousIndex, pdfText ->
			val index = previousIndex + 1
			pdfText.content.indicesOf(symbol.toString()).forEach {
				val symbolIndex = it +
						document.getTextFromLines(index - neighborhoodSize, previousIndex).length +
						neighborhoodSize
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
					println(text)
					rulesViolations.add(RuleViolation(pdfText.line, pdfText.page, name))
				}
			}
		}

		return rulesViolations
	}
}