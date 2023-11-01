package com.github.darderion.mundaneassignmentpolice.checker.rule.regex

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

open class RegexRule(
	val regex: Regex,
	val predicates: List<(matches: List<Pair<String, List<Line>>>) -> List<List<Line>>>,
	val numberOfNearestLinesToSearch: Int,        //without including this line
	type: RuleViolationType,
	area: PDFRegion,
	name: String,
	description: String
) : Rule(area, name, type, description) {
	override fun process(document: PDFDocument): List<RuleViolation> {
		val ruleViolations: MutableList<RuleViolation> = mutableListOf()

		val matches = getAllMatches(document)
		predicates.forEach { predicate ->
			predicate(matches).map { RuleViolation(it, name, type) }.forEach {
				ruleViolations.add(it)
			}
		}
		return ruleViolations
	}

	fun getAllMatches(document: PDFDocument): List<Pair<String, List<Line>>> {
		val matches: MutableList<Pair<String, List<Line>>> = mutableListOf()

		val linesInsideArea = document.text.filter { it.area!! inside area }
		for (lineIndex in 0..(linesInsideArea.size - 1 - numberOfNearestLinesToSearch)) {
			val textFromNearestLines = document.getTextFromLines(
				lineIndex, lineIndex + numberOfNearestLinesToSearch, area
			)

			matches.addAll(regex.findAll(textFromNearestLines).toList().map {
				Pair(
					it.value, linesInsideArea.slice(
						IntRange(lineIndex + textFromNearestLines.slice(
							IntRange(0, it.range.first())
						).count { c -> c == '\n' }, lineIndex + textFromNearestLines.slice(
							IntRange(0, it.range.last())
						).count { c -> c == '\n' })
					)
				)
			})
		}

		return matches.toSet().toList()
	}
}