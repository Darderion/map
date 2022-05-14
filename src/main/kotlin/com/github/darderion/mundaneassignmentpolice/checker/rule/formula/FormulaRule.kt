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

abstract class FormulaRule(
    type: RuleViolationType,
    name: String
) : Rule(PDFRegion.NOWHERE.except(PDFArea.SECTION), name, type) {
    abstract fun getViolations(document: PDFDocument, formulas: List<Formula>): List<RuleViolation>

    override fun process(document: PDFDocument) =
        getViolations(document, getAllFormulas(document))

    private fun getAllFormulas(document: PDFDocument): List<Formula> {
        val text = document.text.filter { it.area!! inside area && it.isNotEmpty() }

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

        return filterFormulas(formulas)
    }

    private fun filterFormulas(formulas: List<Formula>) =
        formulas.filterNot {
            it.text.size == 1 && it.text[0].text == "∗"   // remove single special characters
        }
}
