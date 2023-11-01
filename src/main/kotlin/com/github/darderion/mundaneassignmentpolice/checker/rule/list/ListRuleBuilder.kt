package com.github.darderion.mundaneassignmentpolice.checker.rule.list

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class ListRuleBuilder {
	private var region: PDFRegion = PDFRegion.EVERYWHERE
	private val predicates: MutableList<(list: PDFList<Line>) -> List<Line>> = mutableListOf()
	private var type: RuleViolationType = RuleViolationType.Error
	private var name: String = "Rule name"
	private var description: String =""
	fun disallow(predicate: (list: PDFList<Line>) -> List<Line>) = this.also { predicates.add(predicate) }
	infix fun setDescription(description: String) = this.also { this.description = description }
	infix fun inArea(area: PDFArea) = this.also { region = PDFRegion.NOWHERE.except(area) }

	infix fun inArea(region: PDFRegion) = this.also { this.region = region }

	fun called(name: String) = this.also { this.name = name }

	infix fun type(type: RuleViolationType) = this.also { this.type = type }

	fun getRule() = ListRule(predicates, type, region, name, description)
}
