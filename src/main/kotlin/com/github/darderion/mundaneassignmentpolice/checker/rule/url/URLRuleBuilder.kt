package com.github.darderion.mundaneassignmentpolice.checker.rule.url

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class URLRuleBuilder {
    private val predicates: MutableList<(urls: List<Pair<String, List<Line>>>) -> List<List<Line>>> = mutableListOf()
    private var type: RuleViolationType = RuleViolationType.Error
    private var name: String = "Rule name"
    private var region: PDFRegion = PDFRegion.EVERYWHERE

    fun disallow(predicate: (urls: List<Pair<String, List<Line>>>) -> List<List<Line>>) = this.also { predicates.add(predicate) }

    infix fun called(name: String) = this.also { this.name = name }

    infix fun inArea(area: PDFArea) = this.also { region = PDFRegion.NOWHERE.except(area) }

    infix fun inArea(region: PDFRegion) = this.also { this.region = region }

    fun getRule() = URLRule(predicates, type, region, name)
}
