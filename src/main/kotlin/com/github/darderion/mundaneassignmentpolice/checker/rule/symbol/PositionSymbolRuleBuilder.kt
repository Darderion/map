package com.github.darderion.mundaneassignmentpolice.checker.rule.symbol

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

class PositionSymbolRuleBuilder {
    private var symbol: Char = ' '
    private var xCantBeMore: Int = 600
    private var yCantBeMore: Int = 850
    private var xCantBeLess: Int = 0
    private var yCantBeLess: Int = 0
    private var type: RuleViolationType = RuleViolationType.Error
    private var name: String = "Rule name"
    private var region: PDFRegion = PDFRegion.EVERYWHERE

    infix fun called(name: String) = this.also { this.name = name }

    infix fun inArea(area: PDFArea) = this.also { region = PDFRegion.NOWHERE.except(area) }

    infix fun inArea(region: PDFRegion) = this.also { this.region = region }

    fun xCantBeMore(xPosition: Int) = this.also { this.xCantBeMore = xPosition }

    fun yCantBeMore(yPosition: Int) = this.also { this.yCantBeMore = yPosition }

    fun xCantBeLess(xPosition: Int) = this.also { this.xCantBeLess = xPosition }

    fun yCantBeLess(yPosition: Int) = this.also { this.yCantBeLess = yPosition }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    fun getRule() = PositionSymbolRule(
        symbol,
        xCantBeMore,
        yCantBeMore,
        xCantBeLess,
        yCantBeLess,
        type,
        region,
        name
    ) as SymbolRule
}