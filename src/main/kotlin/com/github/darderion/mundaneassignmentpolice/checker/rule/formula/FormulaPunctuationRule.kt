package com.github.darderion.mundaneassignmentpolice.checker.rule.formula

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Formula
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Word

class FormulaPunctuationRule(
    type: RuleViolationType,
    name: String,
    private val ignoredWords: List<Regex>,
    private val ruleBody:
        (formula: Formula, filteredText: List<Word>, nextFormula: Formula?) -> List<Line>
) : FormulaRule(type, name) {
    override fun getViolations(document: PDFDocument, formulas: List<Formula>): List<RuleViolation> {
        val violations = mutableListOf<RuleViolation>()

        formulas.forEachIndexed { index, formula ->
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

            val nextFormula = formulas.getOrNull(index + 1)

            val violationLines = ruleBody(formula, filteredText, nextFormula)
            if (violationLines.isNotEmpty()) {
                violations.add(
                    RuleViolation(violationLines, name, type)
                )
            }
        }

        return violations
    }
}
