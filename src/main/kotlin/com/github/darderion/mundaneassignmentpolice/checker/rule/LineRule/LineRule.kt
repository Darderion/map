package com.github.darderion.mundaneassignmentpolice.checker.rule.LineRule

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.NOWHERE
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class LineRule(
	val predicates: List<(lines: List<Line>) -> List<Line>>,
	area: PDFRegion,
	type: RuleViolationType,
	name: String,
	description: String
): Rule(area, name, type, description) {
	override fun process(document: PDFDocument): List<RuleViolation> {
		val ruleViolations: MutableSet<RuleViolation> = mutableSetOf()
		val lines = document.text.filter { it.area!! inside(area) }
		predicates.forEach { predicate ->
			ruleViolations.addAll(predicate(lines).map {
				RuleViolation(listOf(it), name, type)
			})
		}
		return ruleViolations.toList()
	}
}
