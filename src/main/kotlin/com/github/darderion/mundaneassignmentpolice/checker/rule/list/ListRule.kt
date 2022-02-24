package com.github.darderion.mundaneassignmentpolice.checker.rule.list

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.SECTION
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.TABLE_OF_CONTENT
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class ListRule(
	val predicates: List<(list: PDFList<Line>) -> List<Line>>,
	type: RuleViolationType,
	area: PDFRegion,
	name: String
	): Rule(area, name, type) {
	override fun process(document: PDFDocument): List<RuleViolation> {
		val rulesViolations: MutableSet<RuleViolation> = mutableSetOf()

		val lists = mutableListOf<PDFList<Line>>()

		if (area.contains(TABLE_OF_CONTENT))
			lists.add(
				document.areas!!.tableOfContents.map {
					document.text.filter { it.area == TABLE_OF_CONTENT }.firstOrNull { line ->
						line.content.contains(it)
					}?: Line(0, 0, 0, listOf(), TABLE_OF_CONTENT)
				}
			)

		if (area.contains(SECTION)) lists.addAll(document.areas!!.lists)

		val pdfLists = lists.map { it.getSublists() }.flatten()

		predicates.forEach { predicate ->
				rulesViolations.addAll(
				pdfLists.map {
					predicate(it)
				}.filter { it.isNotEmpty() }.map {
					RuleViolation(it, name, type)
				}
			)
		}

		return rulesViolations.toList()
	}
}
