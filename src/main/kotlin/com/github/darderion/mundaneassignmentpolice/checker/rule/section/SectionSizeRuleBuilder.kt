package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.ComparisonType
import com.github.darderion.mundaneassignmentpolice.checker.ComparisonType.*
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.SectionName

class SectionSizeRuleBuilder {
    private var ruleName: String = "Rule name"
    private var type: RuleViolationType = RuleViolationType.Error
    private var sections: MutableList<SectionName> = mutableListOf()
    private var pageLimit: Int? = null
    private var percentageLimit: Number? = null
    private var comparisonType: ComparisonType = LESS_THAN
    private var description: String =""

    infix fun called(name: String) = this.also { this.ruleName = name }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    fun sections(vararg names: SectionName) = this.also { this.sections.addAll(names) }

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
        if (limit <= 0) throw IllegalArgumentException("Page limit must be positive")
        this.pageLimit = limit
    }

    infix fun limitByPercentage(limit: Number) = this.also {
        if (limit.toFloat() <= 0 || limit.toFloat() > 100)
            throw IllegalArgumentException("Percentage limit must be positive and less than 100")
        this.percentageLimit = limit
    }

    fun getRule() = SectionSizeRule(
        ruleName,
        type,
        description,
        sections,
        comparisonType,
        pageLimit,
        percentageLimit
    )
}