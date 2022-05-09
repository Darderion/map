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
import com.github.darderion.mundaneassignmentpolice.rules.nodesChecked
import com.github.darderion.mundaneassignmentpolice.rules.results
import com.github.darderion.mundaneassignmentpolice.rules.tasks
import kotlin.text.Typography.section

var taskPages = -1 to -1
var conclusionPages = -1 to -1
fun getPages(document: PDFDocument): Pair < Pair<Int,Int>, Pair<Int,Int> >
{
	var taskPages = -1 to -1
	var conclusionPages = -1 to -1
	var linesIndexes = -1 to -1

	var lines = document.text.filter {
		document.areas!!.sections.forEachIndexed { index , section ->
			if (section.title.contains(Regex("[Зз]адачи")))
				linesIndexes = section.contentIndex to document.areas.sections[index+1].contentIndex
		}
		linesIndexes.first<= it.documentIndex && it.documentIndex < linesIndexes.second
	}.toMutableList()

	if (lines.isNotEmpty())
		taskPages = lines[0].page to lines.last().page

	linesIndexes = -1 to -1
	lines = document.text.filter {
		document.areas!!.sections.forEach {
			if (it.title.contains(Regex("Заключение")))
				linesIndexes = it.contentIndex to -1
		}
		linesIndexes.first <= it.documentIndex
	}.toMutableList()

	if (lines.isNotEmpty())
		conclusionPages = lines[0].page to -1

	return taskPages to conclusionPages
}
fun getTaskFromLines(task: PDFList<Line>): String
{
	var taskText = ""
	for (j in 0 until task.value.size) {
		taskText += if (j == 0)
			task.value[j].text.filterIndexed { index, _ -> index > 2 }.toString().drop(1).dropLast(1)
		else task.value[j].text.toString().drop(1).dropLast(1)

		if (taskText.isNotEmpty() && taskText.last() == '-')
			taskText = taskText.replace(Regex("-"),"")
		else taskText += ", ,"
	}
	return taskText
}
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
		taskPages = -1 to -1
		conclusionPages = -1 to -1
		nodesChecked = false
		tasks = PDFList()
		results = PDFList()
		return rulesViolations.toList()
	}
}
