package com.github.darderion.mundaneassignmentpolice.checker.rulebuilder

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

abstract class NotRegionRuleBuilder<out TBuilder: NotRegionRuleBuilder<TBuilder>>(
        var type: RuleViolationType,
        var name: String,
        var region : PDFRegion
)
{
    fun called(name: String) = apply { this.name = name } as TBuilder
    fun type(type: RuleViolationType)= apply { this.type = type } as TBuilder
    fun inArea(area: PDFArea) = this.also { region = PDFRegion.NOWHERE.except(area) } as TBuilder
    fun inArea(region: PDFRegion) = this.also { this.region = region } as TBuilder

    abstract fun getRule(): Rule
}