package com.github.darderion.mundaneassignmentpolice.checker.rule.section.size

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.section.SectionName
import com.github.darderion.mundaneassignmentpolice.checker.rule.section.SectionRule
import com.github.darderion.mundaneassignmentpolice.checker.rule.section.size.ComparisonType.*

class SectionSizeRuleBuilder {
    private var ruleName: String = "Rule name"
    private var type: RuleViolationType = RuleViolationType.Warning
    private var sectionName: SectionName = SectionName.values().first()
    private var pageLimit: Int? = null
    private var percentageLimit: Number? = null
    private var comparisonType: ComparisonType = LESS_THAN

    infix fun called(name: String) = this.also { this.ruleName = name }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    infix fun section(name: SectionName) = this.also { this.sectionName = name }

    fun shouldBeLessThan() = this.also { this.comparisonType = LESS_THAN }
    fun shouldNotBeLessThan() = this.also { this.comparisonType = GREATER_THAN_OR_EQUAL }

    fun shouldBeLessThanOrEqual() = this.also { this.comparisonType = LESS_THAN_OR_EQUAL }
    fun shouldNotBeLessThanOrEqual() = this.also { this.comparisonType = GREATER_THAN }

    fun shouldBeEqual() = this.also { this.comparisonType = EQUAL }
    fun shouldNotBeEqual() = this.also { this.comparisonType = NOT_EQUAL }

    fun shouldBeGreaterThanOrEqual() = this.also { this.comparisonType = GREATER_THAN_OR_EQUAL }
    fun shouldNotBeGreaterThanOrEqual() = this.also { this.comparisonType = LESS_THAN }

    fun shouldBeGreaterThan() = this.also { this.comparisonType = GREATER_THAN }
    fun shouldNotBeGreaterThan() = this.also { this.comparisonType = LESS_THAN_OR_EQUAL }

    infix fun limitByPages(limit: Int) = this.also {
        if (limit < 0) throw IllegalArgumentException("Page limit must not be negative")
        this.pageLimit = limit
    }

    infix fun limitByPercentage(limit: Number) = this.also {
        if (limit.toDouble() < 0 || limit.toDouble() > 100)
            throw IllegalArgumentException("Percentage limit must be between 0 and 100")
        this.percentageLimit = limit
    }

    fun getRule() = SectionSizeRule(
        ruleName,
        type,
        sectionName,
        comparisonType,
        pageLimit,
        percentageLimit
    ) as SectionRule
}
