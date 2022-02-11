package com.github.darderion.mundaneassignmentpolice.checker

class DocumentReport(val name: String, val ruleViolations: List<RuleViolation>, val errorCode: Int) {
	companion object {
		val emptyFileName = DocumentReport("EmptyFileName", listOf(), -1)
	}
}
