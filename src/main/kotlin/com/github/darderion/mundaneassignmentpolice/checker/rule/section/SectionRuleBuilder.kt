package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section

class SectionRuleBuilder {
    private val predicates: MutableList<(list: List<Section>) -> List<Line>> = mutableListOf()
    private var type: RuleViolationType = RuleViolationType.Error
    private var name: String = "Rule name"

    fun disallow(predicate: (list: List<Section>) -> List<Line>) = this.also { predicates.add(predicate) }

    fun called(name: String) = this.also { this.name = name }

    fun getRule() = SectionRule(predicates, type, name)
}