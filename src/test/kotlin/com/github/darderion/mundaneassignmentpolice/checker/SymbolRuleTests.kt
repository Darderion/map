package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeExactly
import com.github.darderion.mundaneassignmentpolice.TestsConfiguration.Companion.resourceFolder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.and
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.or
import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.symbolbuilder.SymbolRuleBuilder

class SymbolRuleTests: StringSpec({
	"Symbol rule should only search for rule violations in its region" {
		val mediumDash = '–'

		val symbolBuilder = SymbolRuleBuilder<SymbolRuleBuilder<*>>()
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

		(SymbolRuleBuilder<SymbolRuleBuilder<*>>()
			.symbol(longDash)
			.shouldHaveNeighbor(' ')
			.getRule()
				and
                SymbolRuleBuilder<SymbolRuleBuilder<*>>()
					.symbol(longDash)
					.ignoringAdjusting(' ')
					.shouldNotHaveNeighbor(*"0123456789".toCharArray())
					.getRule())
			.process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 2

		(SymbolRuleBuilder<SymbolRuleBuilder<*>>()
			.symbol(longDash)
			.shouldHaveNeighbor(' ')
			.getRule()
				or
				SymbolRuleBuilder<SymbolRuleBuilder<*>>()
					.symbol(longDash)
					.ignoringAdjusting(' ')
					.shouldNotHaveNeighbor(*"0123456789".toCharArray())
					.getRule())
			.process(PDFBox().getPDF(filePathQuestionMarkAndDashes)).count() shouldBeExactly 1
	}
}) {
	private companion object {
		const val filePathQuestionMarkAndDashes = "${resourceFolder}checker/SymbolRuleTestsQuestionMarkAndDashes.pdf"
	}
}
