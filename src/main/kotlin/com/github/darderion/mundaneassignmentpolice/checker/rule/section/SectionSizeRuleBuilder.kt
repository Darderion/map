package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType

class SectionSizeRuleBuilder {
    private var ruleName: String = "Rule name"
    private var type: RuleViolationType = RuleViolationType.Warning
    private var title: SectionTitle = SectionTitle.values().first()
    private var pageLimit: Int? = null
    private var percentageLimit: Number? = null
    private var comparisonType: ComparisonType = ComparisonType.LESS_THAN

    infix fun called(name: String) = this.also { this.ruleName = name }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    infix fun section(title: SectionTitle) = this.also { this.title = title }

    fun shouldBeLessThan() = this.also { this.comparisonType = ComparisonType.LESS_THAN }

    fun shouldBeLessThanOrEqualTo() = this.also { this.comparisonType = ComparisonType.LESS_THAN_OR_EQUAL_TO }

    fun shouldBeEqualTo() = this.also { this.comparisonType = ComparisonType.EQUAL_TO }

    fun shouldBeGreaterThanOrEqualTo() = this.also { this.comparisonType = ComparisonType.GREATER_THAN_OR_EQUAL_TO }

    fun shouldBeGreaterThan() = this.also { this.comparisonType = ComparisonType.GREATER_THAN }

    infix fun pageLimit(limit: Int) = this.also {
        if (limit < 0) throw IllegalArgumentException("Page limit must not be negative")
        this.pageLimit = limit
    }

    infix fun percentageLimit(limit: Number) = this.also {
        if (limit.toDouble() < 0 || limit.toDouble() > 100)
            throw IllegalArgumentException("Percentage limit must be between 0 and 100")
        this.percentageLimit = limit
    }

    fun getRule() = SectionSizeRule(
        ruleName,
        type,
        title,
        comparisonType,
        pageLimit,
        percentageLimit
    ) as SectionRule
}
