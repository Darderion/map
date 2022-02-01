package com.github.darderion.mundaneassignmentpolice.checker.rule.tableofcontent

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text

class TableOfContentRuleBuilder {
	private val predicates: MutableList<(list: List<Text>) -> List<Text>> = mutableListOf()
	private var name: String = "Rule name"

	fun disallow(predicate: (list: List<Text>) -> List<Text>) = this.also { predicates.add(predicate) }

	fun called(name: String) = this.also { this.name = name }

	fun getRule() = TableOfContentRule(predicates, name)
}
