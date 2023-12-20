package com.github.darderion.mundaneassignmentpolice.checker.rule.list

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.SECTION
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.TABLE_OF_CONTENT
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.rules.conclusionWord

/**
 * returns the pages of the section containing the given word
 * @param document pdf document
 * @param word word from section title in string format
 * @return pair<int, int> section page numbers
 */
fun getPages(document: PDFDocument, word : String): Pair<Int,Int>
{
	var pages = -1 to -1
	var linesIndexes = -1 to -1
	val lines = document.text.filter {
		document.areas!!.sections.forEachIndexed { index , section ->
			if (section.title.contains(word) && word != conclusionWord)
				linesIndexes = section.contentIndex to document.areas.sections[index+1].contentIndex
			else if (section.title.contains(word))
				linesIndexes = section.contentIndex to -1
		}
		if (word != conclusionWord)
			linesIndexes.first <= it.documentIndex && it.documentIndex < linesIndexes.second
		else linesIndexes.first <= it.documentIndex
	}.toMutableList()

	if (lines.isNotEmpty() && word != conclusionWord)
		pages = lines[0].page to lines.last().page
	else if (lines.isNotEmpty())
		pages = lines[0].page to -1
	return pages
}
class ListRule(
	val singleListPredicates: MutableList<(list: PDFList<Line>) -> List<Line>> = mutableListOf(),
	val multipleListsPredicates : MutableList<(lists: List<PDFList<Line>>, document: PDFDocument) -> List<Line>> = mutableListOf(),
	val listsFilter : MutableList<(lists: List<PDFList<Line>>,document: PDFDocument) -> MutableList<PDFList<Line>>>,
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
					}?: Line(0, 0, 0, listOf(), TABLE_OF_CONTENT, Coordinate(0,0))
				}
			)

		if (area.contains(SECTION)) lists.addAll(document.areas!!.lists)

		var pdfLists = lists.map { it.getSublists() }.flatten()

		listsFilter.forEach { pdfLists = it(pdfLists, document) }

		singleListPredicates.forEach { predicate ->
				rulesViolations.addAll(
				pdfLists.map {
					predicate(it)
				}.filter { it.isNotEmpty() }.map {
					RuleViolation(it, name, type)
				}
			)
		}

		multipleListsPredicates.forEach { predicate ->
			val lines = predicate(pdfLists, document)
			if (lines.isNotEmpty())
				rulesViolations.add(RuleViolation(lines, name, type))
		}

		println(rulesViolations.toList())
		return rulesViolations.toList()
	}
}
