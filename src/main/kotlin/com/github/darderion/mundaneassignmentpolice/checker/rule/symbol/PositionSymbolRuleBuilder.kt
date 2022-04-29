package com.github.darderion.mundaneassignmentpolice.checker.rule.symbol

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

class PositionSymbolRuleBuilder {
    private var symbol: Char = ' '
    private var maxX: Int = 600
    private var maxY: Int = 850
    private var minX: Int = 0
    private var minY: Int = 0
    private var type: RuleViolationType = RuleViolationType.Error
    private var name: String = "Rule name"
    private var region: PDFRegion = PDFRegion.EVERYWHERE

    infix fun called(name: String) = this.also { this.name = name }

    infix fun inArea(area: PDFArea) = this.also { region = PDFRegion.NOWHERE.except(area) }

    infix fun inArea(region: PDFRegion) = this.also { this.region = region }

    fun xCantBeMoreThan(xPosition: Int) = this.also { this.maxX = xPosition }

    fun yCantBeMoreThan(yPosition: Int) = this.also { this.maxY = yPosition }

    fun xCantBeLessThan(xPosition: Int) = this.also { this.minX = xPosition }

    fun yCantBeLessThan(yPosition: Int) = this.also { this.minY = yPosition }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    fun getRule() = PositionSymbolRule(
        symbol,
        maxX,
        maxY,
        minX,
        minY,
        type,
        region,
        name
    ) as SymbolRule
}