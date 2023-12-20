package com.github.darderion.mundaneassignmentpolice.checker.rule.regex

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class RegexRuleBuilder {
	private var regex: Regex = Regex("")
	private val predicates: MutableList<(matches: List<Pair<String, List<Line>>>) -> List<List<Line>>> = mutableListOf()
	private var numberOfNearestLinesToSearch: Int = 0
	private var type: RuleViolationType = RuleViolationType.Error
	private var region: PDFRegion = PDFRegion.EVERYWHERE.except(PDFArea.CODE)
	private var name: String = "Rule name"

	fun regex(regex: Regex) = this.also { this.regex = regex }

	fun disallow(predicate: (matches: List<Pair<String, List<Line>>>) -> List<List<Line>>) =
		this.also { predicates.add(predicate) }

	fun searchIn(numberOfNearestLines: Int) =
		this.also { this.numberOfNearestLinesToSearch = numberOfNearestLines }

	fun type(type: RuleViolationType) = this.also { this.type = type }

	fun inArea(region: PDFRegion) = this.also { this.region = region }

	fun called(name: String) = this.also { this.name = name }

	fun getRule() = RegexRule(
		regex,
		predicates,
		numberOfNearestLinesToSearch,
		type,
		region,
		name
	)
}