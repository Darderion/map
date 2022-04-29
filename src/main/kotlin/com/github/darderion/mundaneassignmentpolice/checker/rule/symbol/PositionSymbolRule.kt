package com.github.darderion.mundaneassignmentpolice.checker.rule.symbol

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

class PositionSymbolRule(
    symbol: Char,
    private val xCantBeMore: Int,
    private val yCantBeMore: Int,
    private val xCantBeLess: Int,
    private val yCantBeLess: Int,
    type: RuleViolationType,
    area: PDFRegion,
    name: String
) : SymbolRule(symbol, type, area, name) {
    override fun isViolated(document: PDFDocument, line: Int, index: Int): Boolean {
        val xPosition = document.text[line].text.last().position.x
        val yPosition = document.text[line].text.last().position.y
        if (xPosition > xCantBeMore || xPosition < xCantBeLess || yPosition > yCantBeMore || yPosition < yCantBeLess) {
            return true
        }
        return false
    }
}
