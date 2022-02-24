package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

enum class RuleViolationType {
	System, Error, Warning
}

data class RuleViolation(
	val lines: List<Line>,
	val message: String,
	val type: RuleViolationType
) {
	// override fun toString() = if (lines.count() == 1) "[${lines.first().line}, p.${lines.first().page}] --> '$message'" else ""
}
