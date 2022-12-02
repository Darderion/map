package com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.tableofcontentbuilder

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.tableofcontent.TableOfContentRule
import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.RegionRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class TableOfContentRuleBuilder<out TBuilder: TableOfContentRuleBuilder<TBuilder>>(
	type: RuleViolationType = RuleViolationType.Error,
	name: String = "Rule name"
) : RegionRuleBuilder<TBuilder>(type,name, PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT)){

	private var predicates: MutableList<(list: List<Line>) -> List<Line>> = mutableListOf()

	fun disallow(predicate: (list: List<Line>) -> List<Line>) = this.also { predicates.add(predicate) }

	override fun getRule() = TableOfContentRule(predicates, type, name)
}
