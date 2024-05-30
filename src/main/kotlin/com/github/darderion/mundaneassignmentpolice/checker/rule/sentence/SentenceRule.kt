package com.github.darderion.mundaneassignmentpolice.checker.rule.sentence

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class SentenceRule (
        val predicates: MutableList<(list: MutableList<Line>) -> List<Line>>,
        type: RuleViolationType,
        area: PDFRegion,
        name: String,
        description: String
): Rule(area, name, type, description) {

    private val punctuationConclusionSymbols = ".!?”»".toCharArray()

    override fun process(document: PDFDocument): List<RuleViolation> {
        val rulesViolations: MutableList<RuleViolation> = mutableListOf()

        val pdfLines : MutableList<MutableList<Line>> = mutableListOf()
        var currentLines: MutableList<Line> = mutableListOf()
        document.text.filter { it.area!! inside area }.forEach {
            if (it.content.isNotEmpty())
                currentLines.add(it)
            if (it.content.any { it in punctuationConclusionSymbols } )
            {
                pdfLines.add(currentLines)
                currentLines = if (it.content.last() in punctuationConclusionSymbols)
                    mutableListOf() else mutableListOf(it)
            }
        }
        predicates.forEach { predicate ->
            pdfLines.forEach { lines ->
                val predicateLines = predicate(lines)
                if (predicateLines.isNotEmpty() && predicateLines.first().page!=predicateLines.last().page) {
                    rulesViolations.add(RuleViolation(predicateLines
                            .filter { it.page == predicateLines.first().page }, name, type))
                    rulesViolations.add(RuleViolation(predicateLines
                            .filter { it.page != predicateLines.first().page }, name, type))
                }
                else if (predicate(lines).isNotEmpty()) {
                    rulesViolations.add(RuleViolation(predicate(lines), name, type))
                }
            }
        }
        return rulesViolations.toList()
    }
}