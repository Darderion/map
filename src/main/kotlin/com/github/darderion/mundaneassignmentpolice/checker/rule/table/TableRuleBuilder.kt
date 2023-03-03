package com.github.darderion.mundaneassignmentpolice.checker.rule.table

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.tables.Table
import com.github.darderion.mundaneassignmentpolice.pdfdocument.tables.Cell
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class TableRuleBuilder {
    private val predicates: MutableList<(Table) -> List<Cell>> = mutableListOf()
    private var type: RuleViolationType = RuleViolationType.Error
    private var region: PDFRegion = PDFRegion.EVERYWHERE
    private var name: String = "Rule name"

    fun called(name: String) = this.also { this.name = name }

    fun disallow(predicate: (table: Table) -> List<Cell>) = this.also { predicates.add(predicate) }
    fun getRule() = TableRule(predicates, type, region, name)
}