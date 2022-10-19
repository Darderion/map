package com.github.darderion.mundaneassignmentpolice.checker.rule.line

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class LineRule (
        val singleLinePredicates: MutableList<(line: Line) -> List<Line>> = mutableListOf(),
        val multipleLinesPredicates : MutableList<(lines: List<Line>, document: PDFDocument) -> List<Line>>,
        val linesFilters : MutableList <(lines: List<Line>, document: PDFDocument) -> List<Line>>,
        type: RuleViolationType,
        area: PDFRegion,
        name: String
    ): Rule(area, name, type) {

        override fun process(document: PDFDocument): List<RuleViolation> {
            val rulesViolations: MutableSet<RuleViolation> = mutableSetOf()

            var lines = document.text
            linesFilters.forEach { lines = it(lines, document) }

            if (lines.isNotEmpty()) {
                singleLinePredicates.forEach { predicate ->
                    rulesViolations.addAll(
                            lines.map { predicate(it) }
                                    .filter { it.isNotEmpty() }.map {
                                RuleViolation(it, name, type)
                            }
                    )
                }
                multipleLinesPredicates.forEach { predicate ->
                    if (predicate(lines, document).isNotEmpty())
                        rulesViolations.add(RuleViolation(predicate(lines, document), name, type))
                }
            }
            return rulesViolations.toList()
        }
    }