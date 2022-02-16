package com.github.darderion.mundaneassignmentpolice.checker.rule.tableofcontent

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.NOWHERE
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class TableOfContentRule(
	val predicates: List<(tableOfContent: List<Line>) -> List<Line>>,
	name: String
): Rule(NOWHERE.except(PDFArea.TABLE_OF_CONTENT), name) {
	override fun process(document: PDFDocument): List<RuleViolation> {
		val ruleViolations: MutableSet<RuleViolation> = mutableSetOf()
		val tableOfContent = document.text.filter { it.area == PDFArea.TABLE_OF_CONTENT }
		predicates.forEach { predicate ->
			ruleViolations.addAll(predicate(tableOfContent).map {
				RuleViolation(listOf(it), name)
			})
		}
		return ruleViolations.toList()
	}
}
