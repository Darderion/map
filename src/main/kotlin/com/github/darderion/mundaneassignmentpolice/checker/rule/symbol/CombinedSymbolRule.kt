package com.github.darderion.mundaneassignmentpolice.checker.rule.symbol

import com.github.darderion.mundaneassignmentpolice.checker.rule.PredicateType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.checker.rule.PredicateType.*

infix fun SymbolRule.and(otherSymbolRule: SymbolRule) = CombinedSymbolRule(
	listOf(this, otherSymbolRule),
	AND
) as SymbolRule

infix fun SymbolRule.or(otherSymbolRule: SymbolRule) = CombinedSymbolRule(
	listOf(this, otherSymbolRule),
	OR
) as SymbolRule

class CombinedSymbolRule(
	private val rules: List<SymbolRule>,
	private val predicateType: PredicateType
): SymbolRule(
	rules.first().symbol,
	rules.first().type,
	rules.first().area,
	rules.first().name,
	rules.first().description
) {
	override fun isViolated(document: PDFDocument, line: Int, index: Int) = rules.map { it.isViolated(document, line, index) }.reduce {
			acc, ruleViolation ->
		when(predicateType) {
			AND -> acc || ruleViolation
			OR -> acc && ruleViolation
		}
	}
}
