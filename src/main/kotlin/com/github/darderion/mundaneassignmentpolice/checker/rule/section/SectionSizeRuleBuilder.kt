package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType

class SectionSizeRuleBuilder {
    private var name: String = "Rule name"
    private var type: RuleViolationType = RuleViolationType.Warning
    private var sectionName: String = "Section name"
    private var pageLimit: Int = Int.MAX_VALUE
    private var percentageLimit: Number = 100

    infix fun called(name: String) = this.also { this.name = name }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    infix fun section(name: String) = this.also { this.sectionName = name }

    infix fun pageLimit(limit: Int) = this.also {
        if (limit < 0) throw IllegalArgumentException("Page limit must not be negative")
        this.pageLimit = limit
    }

    infix fun percentageLimit(limit: Number) = this.also {
        if (limit.toDouble() < 0 || limit.toDouble() > 100)
            throw IllegalArgumentException("Percentage limit must be between 0 and 100")
        this.percentageLimit = limit
    }

    fun getRule() = SectionSizeRule(name, type, sectionName, pageLimit, percentageLimit) as SectionRule
}
