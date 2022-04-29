package com.github.darderion.mundaneassignmentpolice.checker.rule.symbol
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

class PositionSymbolRule(
    symbol: Char,
    private val maxX: Int,
    private val maxY: Int,
    private val minX: Int,
    private val minY: Int,
    type: RuleViolationType,
    area: PDFRegion,
    name: String
) : SymbolRule(symbol, type, area, name) {
    override fun isViolated(document: PDFDocument, line: Int, index: Int): Boolean {
        val xPosition = document.text[line].text.last().position.x
        val yPosition = document.text[line].text.last().position.y
        return (xPosition > maxX || xPosition < minX || yPosition > maxY || yPosition < minY)
    }
}
