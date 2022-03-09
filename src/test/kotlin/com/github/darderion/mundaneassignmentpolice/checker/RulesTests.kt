package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.TestsConfiguration
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
	"Symbol rule should detect the lack of space around brackets" {
		RULES_SPACE_AROUND_BRACKETS.map { it.process(PDFBox().getPDF(filePathSpaceAroundBrackets)) }
			.flatten()
			.count() shouldBeExactly 16
    }
	"Symbol rule should detect incorrect citation" {
		RULE_CITATION.process(PDFBox().getPDF(filePathCitation)).count() shouldBeExactly 2
	}
	"Section rule should detect sections whose size exceeds specified limit" {

	}
}) {
	companion object {
		const val filePathQuestionMarkAndDashes = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsQuestionMarkAndDashes.pdf"
		const val filePathQuotes = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsQuotes.pdf"
		const val filePathMultipleLinks = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsMultipleLinks.pdf"
		const val filePathLargeRussianLetter = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsLargeRussianLetter.pdf"
		const val filePathSmallNumbers = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsSmallNumbers.pdf"
		const val filePathSpaceAroundBrackets = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsSpaceAroundBrackets.pdf"
		const val filePathCitation = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsCitation.pdf"

		const val filePathIntroductionSize = "${TestsConfiguration.resourceFolder}checker/SectionRuleTestsIntroductionSize.pdf"
		const val filePathProblemStatementSize = "${TestsConfiguration.resourceFolder}checker/SectionRuleTestsProblemStatementSize.pdf"
		const val filePathOverviewSize = "${TestsConfiguration.resourceFolder}checker/SectionRuleTestsOverviewSize.pdf"
		const val filePathConclusionSize = "${TestsConfiguration.resourceFolder}checker/SectionRuleTestsConclusionSize.pdf"
	}
}
