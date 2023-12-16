package com.github.darderion.mundaneassignmentpolice.checker.rule.word

import com.github.darderion.mundaneassignmentpolice.checker.rule.PredicateType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.checker.rule.PredicateType.*

infix fun WordRule.and(otherWordRule: WordRule) = CombinedWordRule(
	listOf(this, otherWordRule),
	AND
) as WordRule

infix fun WordRule.or(otherWordRule: WordRule) = CombinedWordRule(
	listOf(this, otherWordRule),
	OR
) as WordRule

class CombinedWordRule(
	private val rules: List<WordRule>,
	private val predicateType: PredicateType
) : WordRule(
	rules.first().word,
	rules.first().type,
	rules.first().area,
	rules.first().name
) {
	override fun isViolated(document: PDFDocument, line: Int, index: Int) =
		rules.map { it.isViolated(document, line, index) }.reduce { acc, ruleViolation ->
			when (predicateType) {
				AND -> acc || ruleViolation
				OR -> acc && ruleViolation
			}
		}
}

fun combinedWordRules(rules: List<WordRule>): WordRule {
	var combinedRule = rules[0]
	for (i in 1 until rules.size) {
		combinedRule = combinedRule or rules[i]
	}
	return combinedRule
}