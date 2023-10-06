package com.github.darderion.mundaneassignmentpolice.checker.rule.list

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class ListRuleBuilder {
	private var region: PDFRegion = PDFRegion.EVERYWHERE
	private val singleListPredicates: MutableList<(list: PDFList<Line>) -> List<Line>> = mutableListOf()
	private val multipleListsPredicatesWithDocument : MutableList<(lists: List<PDFList<Line>>, document: PDFDocument) -> List<Line>> = mutableListOf()
	private val listsFilter : MutableList <(lists: List<PDFList<Line>>,document: PDFDocument) -> MutableList<PDFList<Line>>> = mutableListOf()
	private var type: RuleViolationType = RuleViolationType.Error
	private var name: String = "Rule name"

	fun disallowInSingleList(predicate: (list: PDFList<Line>) -> List<Line>) = this.also { singleListPredicates.add(predicate) }
	fun disallowInMultipleLists(predicate: (lists: List<PDFList<Line>>, document: PDFDocument) -> List<Line> ) = this.also { multipleListsPredicatesWithDocument.add(predicate) }
	fun addListsFilter (predicate: (lists: List<PDFList<Line>>, document: PDFDocument) -> MutableList<PDFList<Line>>) = this.also { listsFilter.add(predicate) }
	infix fun inArea(area: PDFArea) = this.also { region = PDFRegion.NOWHERE.except(area) }

	infix fun inArea(region: PDFRegion) = this.also { this.region = region }

	fun called(name: String) = this.also { this.name = name }

	infix fun type(type: RuleViolationType) = this.also { this.type = type }

	fun getRule() = ListRule(singleListPredicates,
		multipleListsPredicatesWithDocument, 
		listsFilter,
		type, 
		region, 
		name)
}
