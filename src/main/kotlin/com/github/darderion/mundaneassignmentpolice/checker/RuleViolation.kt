package com.github.darderion.mundaneassignmentpolice.checker

class RuleViolation(val line: Int, val page: Int, val message: String) {
	override fun toString() = "[$line, p.$page] --> '$message'"
}
