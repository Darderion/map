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
	rules.first().ruleBody,
	rules.first().type,
	rules.first().area,
	rules.first().name
) {
	override fun isViolated(document: PDFDocument, line: Int, index: Int,ruleBody: (symbol: Char, document: PDFDocument, line: Int, neighbors: List<Char>, requiredNeighbors: MutableList<Char>, disallowedNeighbors: MutableList<Char>) -> Boolean) = rules.map { it.isViolated(document, line, index,ruleBody) }.reduce {
			acc, ruleViolation ->
		when(predicateType) {
			AND -> acc || ruleViolation
			OR -> acc && ruleViolation
		}
	}
}
