package com.github.darderion.mundaneassignmentpolice.checker.rule

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text

class ListRule(
	val predicates: List<(list: PDFList<Text>) -> Boolean>,
	val area: PDFRegion,
	private val name: String
	) {
	fun process(document: PDFDocument): List<RuleViolation> {
		val rulesViolations: MutableSet<RuleViolation> = mutableSetOf()

		predicates.forEach { predicate ->
			document.areas!!.lists.filter {
				predicate(it)
			}.forEach { list ->
				rulesViolations.add(RuleViolation(list.value, name))
			}
		}

		return rulesViolations.toList()
	}
}
