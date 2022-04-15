package com.github.darderion.mundaneassignmentpolice.checker.rule.formula

import com.github.darderion.mundaneassignmentpolice.checker.PunctuationMark
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.isPunctuationMark
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Formula
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class FormulaPunctuationRule(
    type: RuleViolationType,
    name: String,
    val expectedPunctuationMark: PunctuationMark,
    val indicatorWords: List<Regex>,
    val ignoredWords: List<Regex>
) : FormulaRule(type, name) {
    override fun getLinesOfViolation(document: PDFDocument, formula: Formula): List<Line> {
        val textAfterFormula = formula.lines.last().text
            .takeLastWhile { it != formula.text.last() }
            .toMutableList()

        textAfterFormula.addAll(
            document.text.asSequence().drop(formula.lines.last().documentIndex + 1)
                .filter { it.area!! inside area && it.isNotEmpty() }
                .take(2) // take a line with formula reference and a line with words after the formula
                .map { it.text }.flatten()
        )

        val filteredText = textAfterFormula.filterNot { word -> ignoredWords.any { it.matches(word.text) } }

        val isExpectedSymbolMissing = listOf(
            formula.text.last().text.last(),   // if a punctuation mark is at the end of the formula
            filteredText.getOrNull(0)?.text?.first()
        ).none { it == expectedPunctuationMark.value }

        val wordAfterFormula = when {
            filteredText.isEmpty() -> ""
            filteredText.first().text.first().isPunctuationMark() ->
                filteredText.getOrNull(1)?.text ?: ""
            else -> filteredText.first().text
        }

        if (indicatorWords.any { regex -> regex.matches(wordAfterFormula) } && isExpectedSymbolMissing) {
            return listOf(formula.lines.last())
        }

        return emptyList()
    }
}
