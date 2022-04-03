package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.checker.rule.formula.FormulaRule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Formula
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.rules.RuleSet
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox

class Helper(type: RuleViolationType = RuleViolationType.Error, name: String = "") : FormulaRule(type, name) {
	override fun getLinesOfViolation(document: PDFDocument, formula: Formula) = emptyList<Line>()
}

class Checker {
	fun getRuleViolations(pdfName: String, ruleSet: RuleSet) = getRuleViolations(pdfName, ruleSet.rules)
	fun getRuleViolations(pdfName: String, rules: List<Rule>): List<RuleViolation> {
		val document = PDFBox().getPDF(pdfName)

		val formulaRule = Helper().process(document)

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
}
