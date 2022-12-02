package com.github.darderion.mundaneassignmentpolice.checker.rulebuilder

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion


abstract class RegionRuleBuilder<out TBuilder: RegionRuleBuilder<TBuilder>>(
    var type: RuleViolationType,
    var name: String,
    var region : PDFRegion
)
{
    fun called(name: String) = apply { this.name = name } as TBuilder
    fun type(type: RuleViolationType)= apply { this.type = type } as TBuilder

    abstract fun getRule(): Rule
}