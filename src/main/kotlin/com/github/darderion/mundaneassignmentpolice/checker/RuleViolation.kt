package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

data class RuleViolation(val lines: List<Line>, val message: String) {
	// override fun toString() = if (lines.count() == 1) "[${lines.first().line}, p.${lines.first().page}] --> '$message'" else ""
}
