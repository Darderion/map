package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.checker.rule.list.ListRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRule
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.and
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.or
import com.github.darderion.mundaneassignmentpolice.checker.rule.tableofcontent.TableOfContentRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.statistics.Statistic
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.EVERYWHERE
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.NOWHERE
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
			it.process(document)
		}.flatten().toSet().toList()
	}
	fun getPDFStatistic(pdfName: String) : Statistic {
		val document = PDFBox().getPDF(pdfName)
		return if (document.areas!=null) {
			Statistic(document.getWordsStatistic(),document.getPageStatistic())
		} else Statistic(document.getWordsStatistic())
	}
}
