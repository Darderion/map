package com.github.darderion.mundaneassignmentpolice.checker.rule.line

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class LineRuleBuilder {
	private val predicates: MutableList<(list: List<Line>) -> List<Line>> = mutableListOf()
	private var type: RuleViolationType = RuleViolationType.Error
	private var name: String = "Rule name"
	private var region: PDFRegion = PDFRegion.EVERYWHERE.except(PDFArea.CODE)
	fun inArea(region: PDFRegion) = this.also { this.region = region }
	fun disallow(predicate: (list: List<Line>) -> List<Line>) = this.also { predicates.add(predicate) }
	fun called(name: String) = this.also { this.name = name }

	fun getRule() = LineRule(predicates,region, type, name)
}
