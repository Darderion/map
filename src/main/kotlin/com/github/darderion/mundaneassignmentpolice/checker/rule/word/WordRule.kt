package com.github.darderion.mundaneassignmentpolice.checker.rule.word

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside

abstract class WordRule(
	val word: String,
	type: RuleViolationType,
	area: PDFRegion,
	name: String,
	description: String
): Rule(area, name, type, description) {
	abstract fun isViolated(document: PDFDocument, line: Int, index: Int): Boolean

	override fun process(document: PDFDocument): List<RuleViolation> {
		val rulesViolations: MutableList<RuleViolation> = mutableListOf()

		document.text.filter { it.area!! inside area }.forEachIndexed { lineIndex, line ->
			splitToWordsAndPunctuations(line.content)
				.mapIndexed { wordIndex, wordText -> if (wordText == this.word) wordIndex else -1 }
				.filter { it != -1 }.forEach {
					if (isViolated(document, lineIndex, it)) {
						rulesViolations.add(RuleViolation(listOf(line), name, type))
					}
				}
		}

		return rulesViolations
	}
}