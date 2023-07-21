package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.TestsConfiguration
import com.github.darderion.mundaneassignmentpolice.rules.*
import com.github.darderion.mundaneassignmentpolice.utils.URLUtil
import com.github.darderion.mundaneassignmentpolice.utils.LowQualityConferencesUtil
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify

class RulesTests : StringSpec({
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
	"URLRule should detect shortened URLs" {
		mockkObject(URLUtil)
		every { URLUtil.expand(any()) } returnsArgument 0

		val urls = listOf(
			"https://en.wikipedia.org/wiki/Wikipedia:About" to "https://en.wikipedia.org/wiki/Wikipedia:About",
			"https://t.ly/dgs5" to "https://en.wikipedia.org/wiki/Wikipedia:About",
			"https://google.com" to "https://www.google.com/",
			"https://bit.ly/3tIJmJi." to "https://en.wikipedia.org/wiki/Main_Page",
			"https://en.wikipedia.org" to "https://en.wikipedia.org/wiki/Main_Page",
			"https://bit.ly/3tIJmJi" to "https://en.wikipedia.org/wiki/Main_Page",
			"https://is.gd/gZgSmH" to "https://www.google.com/"
		)

		urls.forEach { (url, expandedUrl) ->
			every { URLUtil.expand(url) } returns expandedUrl
		}

		RULE_SHORTENED_URLS.process(PDFBox().getPDF(filePathShortenedUrls)).count() shouldBeExactly 4
		verify(atLeast = urls.size) { URLUtil.expand(any()) }

		unmockkObject(URLUtil)
	}
	"Rule should detect incorrect symbols in section names" {
		RULE_SYMBOLS_IN_SECTION_NAMES.process(PDFBox().getPDF(filePathSymbolsInSectionNames)).count() shouldBeExactly 4
	}
	"Rule should detect links of different types" {
		RULE_URLS_UNIFORMITY.process(PDFBox().getPDF(filePathUniformityUrls)).count() shouldBeExactly 2
	}
	"Regex rule should detect incorrect order of literature references"{
		RULE_ORDER_OF_REFERENCES.process(PDFBox().getPDF(filePathOrderOfReferences)).count() shouldBeExactly 3
	}
	"Regex rule should detect using different versions of the abbreviation"{
		RULE_VARIOUS_ABBREVIATIONS.process(PDFBox().getPDF(filePathVariousAbbreviations)).count() shouldBeExactly 8
	}
	"Table of content rule should detect incorrect order of sections"{
		RULE_SECTIONS_ORDER.process(PDFBox().getPDF(filePathOrderOfSections)).count() shouldBeExactly 5
	}
  "URLRule should detect links to low quality conferences" {
		mockkObject(LowQualityConferencesUtil)

		val lowQualityConferencesList = listOf(
			"http://www.adpublication.org/",
			"http://www.lifescienceglobal.com/",
			"http://www.ijens.org/"
		)
		every { LowQualityConferencesUtil.getList() } returns lowQualityConferencesList

		RULE_LOW_QUALITY_CONFERENCES.process(PDFBox().getPDF(filePathLowQualityConferences)).count() shouldBeExactly 3

		verify(exactly = 1) { LowQualityConferencesUtil.getList() }

		unmockkObject(LowQualityConferencesUtil)
  }

	"ListRule should detect no results" {
		RULE_TASKS_MAPPING.process(PDFBox().getPDF(filePathNoResults)).count() shouldBeExactly 1
	}
	"ListRule should detect no tasks"{
		RULE_TASKS_MAPPING.process(PDFBox().getPDF(filePathNoTasks)).count() shouldBeExactly 1
	}
	"ListRule should detect no tasks and results" {
		RULE_TASKS_MAPPING.process(PDFBox().getPDF(filePathNoTasksAndResults)).count() shouldBeExactly 2
	}
	"ListRule should detect wrong number of tasks and results"{
		RULE_TASKS_MAPPING.process(PDFBox().getPDF(filePathWrongNumberTasksAndResults)).count() shouldBeExactly 3
	}
}) {
	companion object {
		const val filePathQuestionMarkAndDashes =
			"${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsQuestionMarkAndDashes.pdf"
		const val filePathQuotes = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsQuotes.pdf"
		const val filePathMultipleLinks = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsMultipleLinks.pdf"
		const val filePathLargeRussianLetter =
			"${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsLargeRussianLetter.pdf"
		const val filePathSmallNumbers = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsSmallNumbers.pdf"
		const val filePathSpaceAroundBrackets =
			"${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsSpaceAroundBrackets.pdf"
		const val filePathCitation = "${TestsConfiguration.resourceFolder}checker/SymbolRuleTestsCitation.pdf"
		const val filePathShortenedUrls = "${TestsConfiguration.resourceFolder}checker/URLRuleShortenedUrls.pdf"
		const val filePathUniformityUrls = "${TestsConfiguration.resourceFolder}checker/URLRuleUniformityURL.pdf"
		const val filePathSymbolsInSectionNames =
			"${TestsConfiguration.resourceFolder}checker/RulesTestsSymbolsInSectionNames.pdf"
		const val filePathOrderOfReferences =
			"${TestsConfiguration.resourceFolder}checker/RegexRuleTestsOrderOfReferences.pdf"
		const val filePathVariousAbbreviations =
			"${TestsConfiguration.resourceFolder}checker/RegexRuleTestsVariousAbbreviations.pdf"
		const val filePathOrderOfSections =
			"${TestsConfiguration.resourceFolder}checker/TableOfContentRuleTestsSectionsOrder.pdf"
    	const val filePathLowQualityConferences =
			"${TestsConfiguration.resourceFolder}checker/URLRuleTestsLowQualityConferences.pdf"
		const val filePathNoTasksAndResults =
			"${TestsConfiguration.resourceFolder}checker/NoTasksAndResults.pdf"
		const val filePathNoTasks =
			"${TestsConfiguration.resourceFolder}checker/NoTasks.pdf"
		const val filePathNoResults =
			"${TestsConfiguration.resourceFolder}checker/NoResults.pdf"
		const val filePathWrongNumberTasksAndResults =
			"${TestsConfiguration.resourceFolder}checker/WrongNumberTasksAndResults.pdf"


	}
}
