package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox

class Checker {
	fun getRuleViolations(pdfName: String): List<RuleViolation> {
		val document = PDFBox().getPDF(pdfName)

		val litlinkRule = SymbolRuleBuilder()
			.symbol('?')
			.ignoringAdjusting(*" ,0123456789".toCharArray())
			.shouldNotHaveNeighbor(*"[]".toCharArray())
			.called("Symbol '?' in litlink")
			.getRule()

		val shortDash = '-'

		val shortDashRules = SymbolRuleBuilder()
			.symbol(shortDash)
			.shouldHaveNeighbor(*"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray())
			.shouldHaveNeighbor(*"абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".toCharArray())
			.called("Incorrect usage of '-' symbol")

		val shortDashRuleLeft = shortDashRules
			.fromLeft()
			.getRule()

		val shortDashRuleRight = shortDashRules
			.fromRight()
			.shouldHaveNeighbor('\n')
			.getRule()

		val mediumDash = '–'

		val mediumDashRule = SymbolRuleBuilder()
			.symbol(mediumDash)
			.shouldHaveNeighbor(*"0123456789".toCharArray())
			.called("Incorrect usage of '--' symbol")
			.getRule()

		val longDash = '—'

		val longDashRule = SymbolRuleBuilder()
			.symbol(longDash)
			.ignoringAdjusting(' ')
			.shouldNotHaveNeighbor(*"0123456789".toCharArray())
			.called("Incorrect usage of '---' symbol")
			.getRule()

		return listOf(
			litlinkRule,
			shortDashRuleLeft,
			shortDashRuleRight,
			mediumDashRule,
			longDashRule
		).map {
			it.process(document)
		}.flatten()
	}
}
