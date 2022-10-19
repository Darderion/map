package com.github.darderion.mundaneassignmentpolice.checker.rule.line

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class LineRuleBuilder {
    private var region: PDFRegion = PDFRegion.EVERYWHERE
    private val singleLinePredicates: MutableList<(line: Line) -> List<Line>> = mutableListOf()
    private val multipleLinesPredicates : MutableList<(lines: List<Line>, document: PDFDocument) -> List<Line>> = mutableListOf()
    private val linesFilters : MutableList <(lines: List<Line>, document: PDFDocument) -> List<Line>> = mutableListOf()
    private var type: RuleViolationType = RuleViolationType.Error
    private var name: String = "Rule name"


    fun disallowInSingleLine(predicate: (line: Line) -> List<Line>) = this.also { singleLinePredicates.add(predicate) }

    fun disallowInMultipleLines(predicate: (lines: List<Line>, document: PDFDocument) -> List<Line> ) = this.also { multipleLinesPredicates.add(predicate) }

    fun addLinesFilter (predicate: (lines: List<Line>, document: PDFDocument) -> List<Line>) = this.also { linesFilters.add(predicate) }

    fun called(name: String) = this.also { this.name = name }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    fun getRule() = LineRule(
            singleLinePredicates,
            multipleLinesPredicates,
            linesFilters,
            type,
            region,
            name)
}