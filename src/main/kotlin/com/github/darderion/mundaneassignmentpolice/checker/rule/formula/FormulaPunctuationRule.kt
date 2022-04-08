package com.github.darderion.mundaneassignmentpolice.checker.rule.formula

import com.github.darderion.mundaneassignmentpolice.checker.PunctuationMark
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.isPunctuationMark
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Formula
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class FormulaPunctuationRule(
    type: RuleViolationType,
    name: String,
    val expectedPunctuationMark: PunctuationMark,
    val indicatorWords: List<Regex>,
    val ignoredSymbols: List<Char>
) : FormulaRule(type, name) {
    override fun getLinesOfViolation(document: PDFDocument, formula: Formula): List<Line> {
        var textAfterFormula = formula.lines.last().text.takeLastWhile { it != formula.text.last() }
            .joinToString("") { it.text } + "\n"

        textAfterFormula +=
            document.getLines(formula.lines.last().documentIndex + 1, 2, area)
                .joinToString("\n") { it.content }

        val lastFormulaSymbol = formula.text.last().text.last()
        val firstSymbolAfterFormula = textAfterFormula[0]

        val actualPunctuation = when {
            lastFormulaSymbol.isPunctuationMark() -> lastFormulaSymbol
            firstSymbolAfterFormula.isPunctuationMark() -> {
                textAfterFormula = textAfterFormula.drop(1)
                firstSymbolAfterFormula
            }
            else -> ' '
        }

        val firstWordAfterFormula = textAfterFormula.filterNot { ignoredSymbols.contains(it) }
            .split(" ").first()

        if (indicatorWords.any { regex -> regex.matches(firstWordAfterFormula) }) {
            if (expectedPunctuationMark.value != actualPunctuation) {
                return listOf(formula.lines.last())
            }
        }

        return emptyList()
    }
}
