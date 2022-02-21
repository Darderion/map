package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import com.github.darderion.mundaneassignmentpolice.TestsConfiguration.Companion.resourceFolder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.and
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.or

class SymbolRuleTests: StringSpec({
	"Symbol rule should detect incorrect symbols ? in links" {
		SymbolRuleBuilder()
			.symbol('?')
			.ignoringAdjusting(*" ,0123456789".toCharArray())
			.shouldNotHaveNeighbor(*"[]".toCharArray())
			.getRule()
			.process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 4
	}
	"Symbol rule should detect incorrect usage of - symbol" {
		val shortDash = '-'

		SymbolRuleBuilder()
			.symbol(shortDash)
			.shouldHaveNeighbor(*"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray())
			.shouldHaveNeighbor(*"абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray())
			.getRule().process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 2
	}
	"Symbol rule should detect incorrect usage of -- symbol" {
		val mediumDash = '–'

		SymbolRuleBuilder()
			.symbol(mediumDash)
			.shouldHaveNeighbor(*"0123456789".toCharArray())
			.getRule().process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 3
	}
	"Symbol rule should detect incorrect usage of --- symbol" {
		val longDash = '—'

		SymbolRuleBuilder()
			.symbol(longDash)
			.shouldHaveNeighbor(' ')
			.getRule().process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 1

		SymbolRuleBuilder()
			.symbol(longDash)
			.ignoringAdjusting(' ')
			.shouldNotHaveNeighbor(*"0123456789".toCharArray())
			.getRule().process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 2
	}
	"Symbol rule should only search for rule violations in its region" {
		val mediumDash = '–'

		val symbolBuilder = SymbolRuleBuilder()
			.symbol(mediumDash)
			.shouldHaveNeighbor(*"0123456789".toCharArray())

		for(area in PDFArea.values()) {
			symbolBuilder.inArea(PDFRegion.NOWHERE.except(area))
				.getRule().process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly
					if (area == SECTION) 3 else 0
		}
	}
	"Combined symbol rule should search for rule violations in its region" {
		val longDash = '—'

		(SymbolRuleBuilder()
			.symbol(longDash)
			.shouldHaveNeighbor(' ')
			.getRule()
				and
				SymbolRuleBuilder()
					.symbol(longDash)
					.ignoringAdjusting(' ')
					.shouldNotHaveNeighbor(*"0123456789".toCharArray())
					.getRule())
			.process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 2

		(SymbolRuleBuilder()
			.symbol(longDash)
			.shouldHaveNeighbor(' ')
			.getRule()
				or
				SymbolRuleBuilder()
					.symbol(longDash)
					.ignoringAdjusting(' ')
					.shouldNotHaveNeighbor(*"0123456789".toCharArray())
					.getRule())
			.process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 1
	}
	"Symbol rule should detect using closing quote instead opening quote" {
		val closingQuote = '”'
		val openingQuote = '“'

		SymbolRuleBuilder()
			.symbol(closingQuote)
			.ignoringEveryCharacterExcept(*"$closingQuote$openingQuote".toCharArray())
			.fromLeft().shouldHaveNeighbor(openingQuote)
			.inNeighborhood(20)
			.getRule()
			.process(PDFBox().getPDF(filePathQuotes)).count() shouldBeExactly 4
	}
	"Symbol rule should detect using opening quote instead closing quote" {
		val closingQuote = '”'
		val openingQuote = '“'

		SymbolRuleBuilder()
			.symbol(openingQuote)
			.ignoringEveryCharacterExcept(*"$closingQuote$openingQuote".toCharArray())
			.fromRight().shouldHaveNeighbor(closingQuote)
			.inNeighborhood(20)
			.getRule()
			.process(PDFBox().getPDF(filePathQuotes)).count() shouldBeExactly 2
	}
}) {
	private companion object {
		const val filePathQuestionMarkAndDashes = "${resourceFolder}checker/SymbolRuleTestsQuestionMarkAndDashes.pdf"
		const val filePathQuotes = "${resourceFolder}checker/SymbolRuleTestsQuotes.pdf"
	}
}
