package com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.urlbuilder

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.url.URLRule
import com.github.darderion.mundaneassignmentpolice.checker.rule.url.Url
import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.NotRegionRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class URLRuleBuilder<out TBuilder: URLRuleBuilder<TBuilder>>(
    type: RuleViolationType = RuleViolationType.Error,
    name: String = "Rule name",
    region: PDFRegion = PDFRegion.EVERYWHERE
) : NotRegionRuleBuilder<TBuilder>(type,name, region){

    private val predicates: MutableList<(urls: List<Url>) -> List<Pair<Url, List<Line>>>> = mutableListOf()

    fun disallow(predicate: (urls: List<Url>) -> List<Pair<Url, List<Line>>>) = this.also { predicates.add(predicate) }

    override fun getRule() = URLRule(predicates, type, region, name)
}