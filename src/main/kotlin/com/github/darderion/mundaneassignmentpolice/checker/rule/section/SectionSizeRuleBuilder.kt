package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType

class SectionSizeRuleBuilder {
    private var name: String = "Слишком длинная секция"
    private var type: RuleViolationType = RuleViolationType.Warning
    private var sectionName: String = "Section name"
    private var pageLimit: Int = Int.MAX_VALUE
    private var percentageLimit: Int = 100

    infix fun called(name: String) = this.also { this.name = name }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    infix fun section(name: String) = this.also { this.sectionName = name }

    infix fun pageLimit(limit: Int) = this.also { this.pageLimit = limit }

    infix fun percentageLimit(limit: Int) = this.also { this.percentageLimit = limit }

    fun getRule() = SectionSizeRule(name, type, sectionName, pageLimit, percentageLimit)
}