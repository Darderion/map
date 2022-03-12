package com.github.darderion.mundaneassignmentpolice.checker.rule.url

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class URLRuleBuilder {
    private val predicates: MutableList<(urls: List<Pair<String, Line>>) -> List<Line>> = mutableListOf()
    private var type: RuleViolationType = RuleViolationType.Error
    private var name: String = "Rule name"

    fun disallow(predicate: (urls: List<Pair<String, Line>>) -> List<Line>) = this.also { predicates.add(predicate) }

    infix fun called(name: String) = this.also { this.name = name }

    fun getRule() = URLRule(predicates, type, name)
}