package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.list.ListRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.and
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.EVERYWHERE
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.NOWHERE
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import java.util.*

class Checker {
	private val enLetters = "abcdefghijklmnopqrstuvwxyz"
	private val enCapitalLetters = enLetters.uppercase(Locale.getDefault())
	private val EN = enLetters + enCapitalLetters

	private val rusLetters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
	private val rusCapitalLetters = rusLetters.uppercase(Locale.getDefault())
	private val RU = rusLetters + rusCapitalLetters

	private val numbers = "0123456789"

	fun getRuleViolations(pdfName: String): List<RuleViolation> {
		val document = PDFBox().getPDF(pdfName)

		if (document.areas == null) return listOf()

		val litlinkRule = SymbolRuleBuilder()
			.symbol('?')
			.ignoringAdjusting(*" ,$numbers".toCharArray())
			.shouldNotHaveNeighbor(*"[]".toCharArray())
			.called("Symbol '?' in litlink")
			.getRule()

		val shortDash = '-'

		// 3-way road
		// one-sided battle

		val shortDashRules = SymbolRuleBuilder()
			.symbol(shortDash)
			.shouldHaveNeighbor(*EN.toCharArray())
			.shouldHaveNeighbor(*RU.toCharArray())
			.called("Incorrect usage of '-' symbol")
			.inArea(EVERYWHERE.except(BIBLIOGRAPHY, FOOTNOTE))

		val shortDashRule = shortDashRules
			.fromLeft().shouldHaveNeighbor(*numbers.toCharArray(), '.').getRule() and shortDashRules
			.fromRight().shouldHaveNeighbor('\n').getRule()

		val mediumDash = '–'

		val mediumDashRule = SymbolRuleBuilder()
			.symbol(mediumDash)
			.shouldHaveNeighbor(*numbers.toCharArray())
			.called("Incorrect usage of '--' symbol")
			.inArea(EVERYWHERE.except(BIBLIOGRAPHY, FOOTNOTE))
			.ignoringIfIndex(0)
			.getRule()

		val longDash = '—'

		val longDashRule = SymbolRuleBuilder()
			.symbol(longDash)
			.ignoringAdjusting(' ')
			.shouldNotHaveNeighbor(*numbers.toCharArray())
			.called("Incorrect usage of '---' symbol")
			.inArea(EVERYWHERE.except(BIBLIOGRAPHY, FOOTNOTE))
			.getRule()

		val listRule = ListRuleBuilder()
			.inArea(NOWHERE.except(TABLE_OF_CONTENT))
			.called("Only 1 subsection in a section")
			.disallow {
				if (it.nodes.count() == 1) it.nodes.first().getText() else listOf()
			}.getRule()

		return listOf(
			litlinkRule,
			shortDashRule,
			mediumDashRule,
			longDashRule,
			listRule
		).map {
			it.process(document)
		}.flatten().toSet().toList()
	}
}
