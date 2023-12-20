package com.github.darderion.mundaneassignmentpolice.checker.rule.url

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class URLRuleBuilder {
    private val predicates: MutableList<(urls: List<Url>) -> List<Pair<Url, List<Line>>>> = mutableListOf()
    private val predicatesOfIgnoring: MutableList<(url: Url) -> Boolean> = mutableListOf()
    private var type: RuleViolationType = RuleViolationType.Error
    private var name: String = "Rule name"
    private var region: PDFRegion = PDFRegion.EVERYWHERE.except(PDFArea.CODE)

    fun disallow(predicate: (urls: List<Url>) -> List<Pair<Url, List<Line>>>) = this.also { predicates.add(predicate) }

    fun ignoreIf(predicate: (url: Url) -> Boolean) = this.also { predicatesOfIgnoring.add(predicate) }

    infix fun called(name: String) = this.also { this.name = name }

    infix fun inArea(area: PDFArea) = this.also { region = PDFRegion.NOWHERE.except(area) }

    infix fun inArea(region: PDFRegion) = this.also { this.region = region }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    fun getRule() = URLRule(predicates, predicatesOfIgnoring, type, region, name)
}
