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
			.process(PDFBox().getPDF(filePath)).count() shouldBeExactly 4
	}
	"Symbol rule should detect incorrect usage of - symbol" {
		val shortDash = '-'

		SymbolRuleBuilder()
			.symbol(shortDash)
			.shouldHaveNeighbor(*"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray())
			.shouldHaveNeighbor(*"абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray())
			.getRule().process(PDFBox().getPDF(filePath)).count() shouldBeExactly 2
	}
	"Symbol rule should detect incorrect usage of -- symbol" {
		val mediumDash = '–'

		SymbolRuleBuilder()
			.symbol(mediumDash)
			.shouldHaveNeighbor(*"0123456789".toCharArray())
			.getRule().process(PDFBox().getPDF(filePath)).count() shouldBeExactly 3
	}
	"Symbol rule should detect incorrect usage of --- symbol" {
		val longDash = '—'

		SymbolRuleBuilder()
			.symbol(longDash)
			.shouldHaveNeighbor(' ')
			.getRule().process(PDFBox().getPDF(filePath)).count() shouldBeExactly 1

		SymbolRuleBuilder()
			.symbol(longDash)
			.ignoringAdjusting(' ')
			.shouldNotHaveNeighbor(*"0123456789".toCharArray())
			.getRule().process(PDFBox().getPDF(filePath)).count() shouldBeExactly 2
	}
	"Symbol rule should only search for rule violations in its region" {
		val mediumDash = '–'

		val symbolBuilder = SymbolRuleBuilder()
			.symbol(mediumDash)
			.shouldHaveNeighbor(*"0123456789".toCharArray())

		for(area in PDFArea.values()) {
			symbolBuilder.inArea(PDFRegion.NOWHERE.except(area))
				.getRule().process(PDFBox().getPDF(filePath)).count() shouldBeExactly
					if (area == SECTION) 3 else 0
		}
	}
	"Symbol rule should detect incorrect using multiple links" {
		SymbolRuleBuilder()
			.symbol(']')
			.ignoringAdjusting(*" ,".toCharArray())
			.fromRight().shouldNotHaveNeighbor(*"[".toCharArray())
			.getRule()
			.process(PDFBox().getPDF(filePath2)).count() shouldBeExactly 3
	}
	"Symbol rule should detect incorrect using multiple links" {
		SymbolRuleBuilder()
			.symbol('(')
			.ignoringAdjusting(*" ".toCharArray())
			.fromRight().shouldNotHaveNeighbor(*"АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray())
			.getRule()
			.process(PDFBox().getPDF(filePath3)).count() shouldBeExactly 3
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
			.process(PDFBox().getPDF(filePath)).count() shouldBeExactly 2

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
			.process(PDFBox().getPDF(filePath)).count() shouldBeExactly 1
	}
}) {
	private companion object {
		const val filePath = "${resourceFolder}checker/SymbolRuleTests.pdf"
		const val filePath2 = "${resourceFolder}checker/MultipleLinksRuleTest.pdf"
		const val filePath3 = "${resourceFolder}checker/LargeRussianLetterTests.pdf"
	}
}
