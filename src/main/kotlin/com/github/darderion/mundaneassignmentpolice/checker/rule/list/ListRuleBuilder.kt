package com.github.darderion.mundaneassignmentpolice.checker.rule.list

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text

class ListRuleBuilder {
	private var region: PDFRegion = PDFRegion.EVERYWHERE
	private val predicates: MutableList<(list: PDFList<Text>) -> List<Text>> = mutableListOf()
	private var name: String = "Rule name"

	fun disallow(predicate: (list: PDFList<Text>) -> List<Text>) = this.also { predicates.add(predicate) }

	infix fun inArea(area: PDFArea) = this.also { region = PDFRegion.NOWHERE.except(area) }

	infix fun inArea(region: PDFRegion) = this.also { this.region = region }

	fun called(name: String) = this.also { this.name = name }

	fun getRule() = ListRule(predicates, region, name)
}
