package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.ListRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.EVERYWHERE
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.NOWHERE
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox

class Checker {
	fun getRuleViolations(pdfName: String): List<RuleViolation> {
		val document = PDFBox().getPDF(pdfName)

		if (document.areas == null) return listOf()

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
			.inArea(EVERYWHERE.except(BIBLIOGRAPHY, FOOTNOTE))

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
			.inArea(EVERYWHERE.except(BIBLIOGRAPHY, FOOTNOTE))
			.getRule()

		val longDash = '—'

		val longDashRule = SymbolRuleBuilder()
			.symbol(longDash)
			.ignoringAdjusting(' ')
			.shouldNotHaveNeighbor(*"0123456789".toCharArray())
			.called("Incorrect usage of '---' symbol")
			.inArea(EVERYWHERE.except(BIBLIOGRAPHY, FOOTNOTE))
			.getRule()

		val listRule = ListRuleBuilder()
			.inArea(NOWHERE.except(TABLE_OF_CONTENT, SECTION))
			.disallow {
				if (it.nodes.count() == 1) it.nodes.first().getText() else listOf()
			}.getRule()

		return listOf(
			litlinkRule,
			shortDashRuleLeft,
			shortDashRuleRight,
			mediumDashRule,
			longDashRule,
			listRule
		).map {
			it.process(document)
		}.flatten()
	}
}
