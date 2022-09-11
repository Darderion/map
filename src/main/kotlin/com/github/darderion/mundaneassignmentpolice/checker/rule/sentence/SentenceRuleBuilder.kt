package com.github.darderion.mundaneassignmentpolice.checker.rule.sentence
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line


class SentenceRuleBuilder {
    private val predicates: MutableList<(list: MutableList<Line>) -> List<Line>> = mutableListOf()
    private var type: RuleViolationType = RuleViolationType.Warning
    private var region: PDFRegion = PDFRegion.NOWHERE.except(PDFArea.SECTION)
    private var name: String = "Rule name"

    infix fun called(name: String) = this.also { this.name = name }

    infix fun inArea(area: PDFArea) = this.also { region = PDFRegion.NOWHERE.except(area) }

    infix fun inArea(region: PDFRegion) = this.also { this.region = region }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    fun disallow(predicate: (list: MutableList<Line>) -> List<Line>) = this.also { predicates.add(predicate) }

    fun getRule() = SentenceRule(
        predicates,
        type,
        region,
        name
    ) as Rule
}