package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.checker.rule.list.conclusionPages
import com.github.darderion.mundaneassignmentpolice.checker.rule.list.getPages
import com.github.darderion.mundaneassignmentpolice.checker.rule.list.taskPages
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.rules.RULE_TASK_MAPPING
import com.github.darderion.mundaneassignmentpolice.rules.RuleSet
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import java.util.*

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
			if (it == RULE_TASK_MAPPING)
			{
				val pages = getPages(document)
				taskPages = pages.first
				conclusionPages= pages.second
			}
			it.process(document)
		}.flatten().toSet().toList()
	}
}
