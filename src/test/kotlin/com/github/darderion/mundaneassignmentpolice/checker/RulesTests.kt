package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.TestsConfiguration
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRule
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.rules.*
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly

class RulesTests: StringSpec({
	"Symbol rule should detect incorrect symbols ? in links" {
		RULE_LITLINK.process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 4
	}
	"Symbol rule should detect incorrect usage of - symbol" {
		RULE_SHORT_DASH.process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 2
	}
	"Symbol rule should detect incorrect usage of -- symbol" {
		RULE_MEDIUM_DASH.process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 3
	}
	"Symbol rule should detect incorrect usage of --- symbol" {
		RULE_LONG_DASH.process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 2
	}
	"Symbol rule should detect use of closing quote instead opening quote" {
		RULE_CLOSING_QUOTATION.process(PDFBox().getPDF(filePathQuotes)).count() shouldBeExactly 4
	}
	"Symbol rule should detect use of opening quote instead closing quote" {
		RULE_OPENING_QUOTATION.process(PDFBox().getPDF(filePathQuotes)).count() shouldBeExactly 2
	}
	"Symbol rule should detect incorrect use of multiple links" {
		RULE_MULTIPLE_LITLINKS.process(PDFBox().getPDF(filePathMultipleLinks)).count() shouldBeExactly 3
	}
	"Symbol rule should detect incorrect usage of capital letters" {
		RULE_BRACKETS_LETTERS.process(PDFBox().getPDF(filePathLargeRussianLetter)).count() shouldBeExactly 2
	}
	"Symbol rule should detect writing integers from one to nine as digits instead of words" {
		RULES_SMALL_NUMBERS.sumOf { it.process(PDFBox().getPDF(filePathSmallNumbers)).count() } shouldBeExactly 6
	}
}) {
	companion object {
		const val filePathQuestionMarkAndDashes = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsQuestionMarkAndDashes.pdf"
		const val filePathQuotes = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsQuotes.pdf"
		const val filePathMultipleLinks = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsMultipleLinks.pdf"
		const val filePathLargeRussianLetter = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsLargeRussianLetter.pdf"
		const val filePathSmallNumbers = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsSmallNumbers.pdf"
	}
}
