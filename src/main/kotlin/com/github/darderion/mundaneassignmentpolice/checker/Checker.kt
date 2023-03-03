package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.checker.rule.table.TableRule
import com.github.darderion.mundaneassignmentpolice.rules.RuleSet
import com.github.darderion.mundaneassignmentpolice.rules.TableRuleSet
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox

class Checker {
	fun getRuleViolations(pdfName: String, ruleSet: RuleSet) = getRuleViolations(pdfName, ruleSet.rules)
	fun getRuleViolations(pdfName: String, rules: List<Rule>): List<RuleViolation> {
		val document = PDFBox().getPDF(pdfName)
		if (document.areas == null) return listOf(
			RuleViolation(
				listOf(document.text.first()),
				"PDFArea",
				RuleViolationType.System
			)
		)

		return rules.map {
			it.process(document)
		}.flatten().toSet().toList()
	}
	fun getRuleTableViolations(pdfName: String, ruleSet: TableRuleSet) = getRuleTableViolations(pdfName, ruleSet.rules)

	fun getRuleTableViolations(pdfName: String, rules: List<TableRule>): List<RuleTableViolation>{
		val document = PDFBox().getPDF(pdfName)

		return rules.map {
			it.process(document)
		}.flatten().toSet().toList()
	}
}
