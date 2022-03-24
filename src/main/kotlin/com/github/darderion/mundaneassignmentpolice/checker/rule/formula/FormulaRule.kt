package com.github.darderion.mundaneassignmentpolice.checker.rule.formula

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.*
import com.github.darderion.mundaneassignmentpolice.utils.nearby
import kotlin.math.absoluteValue

abstract class FormulaRule(
    type: RuleViolationType,
    name: String
): Rule(PDFRegion.NOWHERE.except(PDFArea.SECTION), name, type) {
    abstract fun getLinesOfViolation(document: PDFDocument, formula: List<Line>): List<Line>

    override fun process(document: PDFDocument): List<RuleViolation> {
        val rulesViolations = mutableListOf<RuleViolation>()
        val formulas = getAllFormulas(document)

        formulas.forEach {
            rulesViolations.add(RuleViolation(getLinesOfViolation(document, it), name, type))
        }

        return rulesViolations
    }

    private fun getAllFormulas(document: PDFDocument): List<List<Line>> {
        val normalLineIndents = getNormalIndents(document)
        val normalLineInterval = getNormalInterval(document)

        val text = document.text.filter { it.area!! inside area }.toMutableList()
        // Adding a line to process a text that has no lines after a formula
        val additionalLine = Line(-1, -1, -1,
            listOf(Word("NOT A FORMULA LINE", Font(), Coordinate(normalLineIndents.first(), -normalLineInterval - 1)))
        )
        text += additionalLine

        val formulas = mutableListOf<List<Line>>()
        val formula = mutableListOf<Line>()
        var lastNormalLine = additionalLine
        for (line in text) {
            if (normalLineIndents.any { line.position.x nearby it }) {
               lastNormalLine = line
                if (formula.isNotEmpty()) {
                   formulas.add(formula)
                   formula.clear()
               }
            }
            else {
                val interval = lastNormalLine.text.maxOf { it.position.y } - line.text.minOf { it.position.y }
                // Excludes captions to figures and entries in tables
                if (line.text.any { it.font.type == PostScriptFontType.TYPE2 }) {
                    // Excludes multiline formulas within a single line of common text
                    if (interval > normalLineInterval || lastNormalLine.page != line.page)
                        formula.add(line)
                }
            }
        }
        return formulas
    }

    private fun getNormalIndents(document: PDFDocument) = document.areas!!.sections.first()
        .run {
            listOf(titleIndex, contentIndex) +
                document.areas.lists.map { it.getText() }.flatten().map { it.documentIndex }
        }.map { document.text[it].position.x }.toSet()

    private fun getNormalInterval(document: PDFDocument) = document.areas!!.sections.first()
        .run { listOf(contentIndex + 1, contentIndex) }
        .map { document.text[it].position.y }
        .run { first() - last() }.absoluteValue
}
