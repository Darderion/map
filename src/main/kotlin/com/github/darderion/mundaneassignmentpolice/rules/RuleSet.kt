package com.github.darderion.mundaneassignmentpolice.rules

import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule

val RULE_SET_RU = RuleSet(mutableListOf(
	RULE_LITLINK,
	RULE_SHORT_DASH,
	RULE_MEDIUM_DASH,
	RULE_LONG_DASH,
	RULE_CLOSING_QUOTATION,
	RULE_OPENING_QUOTATION,
	RULE_MULTIPLE_LITLINKS,
	RULE_BRACKETS_LETTERS,
	RULE_SINGLE_SUBSECTION,
	RULE_TABLE_OF_CONTENT_NUMBERS
).also { it.addAll(RULES_SMALL_NUMBERS) })

class RuleSet(val rules: List<Rule>) {
}
