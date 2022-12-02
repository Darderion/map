package com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.regexbuilder

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.regex.RegexRule
import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.NotRegionRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class RegexRuleBuilder<out TBuilder: RegexRuleBuilder<TBuilder>>(
        type: RuleViolationType = RuleViolationType.Error,
        name: String = "Rule name",
        region: PDFRegion = PDFRegion.EVERYWHERE
) : NotRegionRuleBuilder<TBuilder>(type,name, region){
	private var regex: Regex = Regex("")
	private val predicates: MutableList<(matches: List<Pair<String, List<Line>>>) -> List<List<Line>>> = mutableListOf()
	private var numberOfNearestLinesToSearch: Int = 0

	fun regex(regex: Regex) = this.also { this.regex = regex }

	fun disallow(predicate: (matches: List<Pair<String, List<Line>>>) -> List<List<Line>>) =
		this.also { predicates.add(predicate) }

	fun searchIn(numberOfNearestLines: Int) =
		this.also { this.numberOfNearestLinesToSearch = numberOfNearestLines }


	override fun getRule() = RegexRule(
		regex,
		predicates,
		numberOfNearestLinesToSearch,
		type,
		region,
		name
	)
}