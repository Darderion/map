package com.github.darderion.mundaneassignmentpolice.checker.rule.tableofcontent

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class TableOfContentRuleBuilder {
	private val predicates: MutableList<(list: List<Line>) -> List<Line>> = mutableListOf()
	private var name: String = "Rule name"

	fun disallow(predicate: (list: List<Line>) -> List<Line>) = this.also { predicates.add(predicate) }

	fun called(name: String) = this.also { this.name = name }

	fun getRule() = TableOfContentRule(predicates, name)
}
