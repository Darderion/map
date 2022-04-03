package com.github.darderion.mundaneassignmentpolice.checker.rule.formula

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Formula
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.PostScriptFontType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Word
import kotlin.math.absoluteValue

abstract class FormulaRule(
    type: RuleViolationType,
    name: String
): Rule(PDFRegion.NOWHERE.except(PDFArea.SECTION), name, type) {
    abstract fun getLinesOfViolation(document: PDFDocument, formula: Formula): List<Line>

    override fun process(document: PDFDocument): List<RuleViolation> {
        val rulesViolations = mutableListOf<RuleViolation>()
        val formulas = getAllFormulas(document)

        formulas.forEach {
            rulesViolations.add(RuleViolation(getLinesOfViolation(document, it), name, type))
        }

        return rulesViolations
    }

    private fun getAllFormulas(document: PDFDocument): List<Formula> {
        /*val normalLineIndents = getNormalIndents(document)
        val normalLineInterval = getNormalInterval(document)

        // Adding a line to process a text that has no lines after a formula
        val additionalLine = Line(-1, -1, -1,
            listOf(Word("NOT A FORMULA LINE", Font(), Coordinate(normalLineIndents.first(), -normalLineInterval - 1)))
        )*/
        val text = document.text.filter { it.area!! inside area && it.isNotEmpty() } /*+ additionalLine*/

        /*val formulas = mutableListOf<List<Line>>()
        val formula = mutableListOf<Line>()
        val isFormula = false
        var lastNormalLine = additionalLine
        for (line in text) {
            if (normalLineIndents.any { line.position.x nearby it }) {
               lastNormalLine = line
                if (formula.isNotEmpty()) {
                   formulas.add(formula)
                   formula.clear()
               }
            } else {
                val interval = lastNormalLine.text.maxOf { it.position.y } - line.text.minOf { it.position.y }
                // Excludes captions to figures and entries in tables
                if (line.text.any { it.font.type == PostScriptFontType.TYPE2 }) {
                    // Excludes multiline formulas within a single line of common text
                    if (interval > normalLineInterval || lastNormalLine.page != line.page)
                        formula.add(line)
                }
            }
        }*/

        val formulas = mutableListOf<Formula>()
        val formulaText = mutableListOf<Word>()
        val formulaLines = mutableSetOf<Line>()

        text.forEach { line ->
            line.text.forEach { word ->
                if (word.font.type == PostScriptFontType.TYPE2 || word.text == " " && formulaText.isNotEmpty()) {
                    formulaText.add(word)
                    formulaLines.add(line)
                } else if (formulaText.isNotEmpty()) {
                    formulas.add(Formula(formulaText.dropLastWhile { it.text == " " }, formulaLines.toSet()))
                    formulaText.clear()
                    formulaLines.clear()
                }
            }
        }
        if (formulaText.isNotEmpty())
            formulas.add(Formula(formulaText.dropLastWhile { it.text == " " }, formulaLines.toSet()))

        return formulas
    }

    private fun getNormalIndents(document: PDFDocument) = document.areas!!.sections.first()
        .let { section ->
            listOf(section.titleIndex, section.contentIndex) +
                document.areas.lists.map { it.getText() }.flatten().map { it.documentIndex }
        }.map { document.text[it].position.x }.toSet()

    private fun getNormalInterval(document: PDFDocument) = document.areas!!.sections.first()
        .let { listOf(it.contentIndex + 1, it.contentIndex) }
        .map { document.text[it].position.y }
        .let { it.first() - it.last() }.absoluteValue
}
