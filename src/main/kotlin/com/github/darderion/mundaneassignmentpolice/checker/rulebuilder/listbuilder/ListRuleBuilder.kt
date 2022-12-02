package com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.listbuilder

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.list.ListRule
import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.NotRegionRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class ListRuleBuilder<out TBuilder: ListRuleBuilder<TBuilder>>(
		type: RuleViolationType = RuleViolationType.Error,
		name: String = "Rule name",
		region: PDFRegion = PDFRegion.EVERYWHERE
) : NotRegionRuleBuilder<TBuilder>(type,name, region){

	private var predicates: MutableList<(list: PDFList<Line>) -> List<Line>> = mutableListOf()

	fun disallow(predicate: (list: PDFList<Line>) -> List<Line>) = this.also { predicates.add(predicate) }

	override fun getRule() = ListRule(predicates, type, region, name)
}