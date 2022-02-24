package com.github.darderion.mundaneassignmentpolice.checker.rule.symbol

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside

abstract class SymbolRule(
	val symbol: Char,
	type: RuleViolationType,
	area: PDFRegion,
	name: String
): Rule(area, name, type) {
	abstract fun isViolated(document: PDFDocument, line: Int, index: Int): Boolean

	override fun process(document: PDFDocument): List<RuleViolation> {
		val rulesViolations: MutableList<RuleViolation> = mutableListOf()

		document.text.filter { it.area!! inside area }.forEachIndexed { lineIndex, pdfText ->
			val pdfText = document.text.filter { it.area!! inside area }[lineIndex]
			pdfText.content.indicesOf(symbol.toString()).forEach {
				if (isViolated(document, lineIndex, it)) {
					rulesViolations.add(RuleViolation(listOf(pdfText), name, type))
				}
			}
		}

		return rulesViolations
	}
}