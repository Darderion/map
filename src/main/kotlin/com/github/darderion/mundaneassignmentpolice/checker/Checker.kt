package com.github.darderion.mundaneassignmentpolice.checker

import com.github.darderion.mundaneassignmentpolice.checker.rule.list.ListRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.and
import com.github.darderion.mundaneassignmentpolice.checker.rule.tableofcontent.TableOfContentRuleBuilder
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

		if (document.areas == null) return listOf(
			RuleViolation(
				listOf(document.text.first()),
				"PDFArea"
			)
		)

		val litlinkRule = SymbolRuleBuilder()
			.symbol('?')
			.ignoringAdjusting(*" ,$numbers".toCharArray())
			.shouldNotHaveNeighbor(*"[]".toCharArray())
			//.called("Symbol '?' in litlink")
			.called("Символ ? в ссылке на литературу")
			.getRule()

		val shortDash = '-'

		// 3-way road
		// one-sided battle

		val shortDashRules = SymbolRuleBuilder()
			.symbol(shortDash)
			.shouldHaveNeighbor(*EN.toCharArray())
			.shouldHaveNeighbor(*RU.toCharArray())
			//.called("Incorrect usage of '-' symbol")
			.called("Неправильное использование дефиса")
			.inArea(EVERYWHERE.except(BIBLIOGRAPHY, FOOTNOTE))

		val shortDashRule = shortDashRules
			.fromLeft().shouldHaveNeighbor(*numbers.toCharArray(), '.').getRule() and shortDashRules
			.fromRight().shouldHaveNeighbor('\n').getRule()

		val mediumDash = '–'

		val mediumDashRule = SymbolRuleBuilder()
			.symbol(mediumDash)
			.shouldHaveNeighbor(*numbers.toCharArray())
			//.called("Incorrect usage of '--' symbol")
			.called("Неправильное использование короткого тире")
			.inArea(EVERYWHERE.except(BIBLIOGRAPHY, FOOTNOTE))
			.ignoringIfIndex(0)
			.getRule()

		val longDash = '—'

		val longDashRule = SymbolRuleBuilder()
			.symbol(longDash)
			.ignoringAdjusting(' ')
			.shouldNotHaveNeighbor(*numbers.toCharArray())
			//.called("Incorrect usage of '---' symbol")
			.called("Неправильное использование длинного тире")
			.inArea(EVERYWHERE.except(BIBLIOGRAPHY, FOOTNOTE))
			.getRule()


		val squareClosingBracket=']'
		val squareOpeningBracket='['

		val multiLinksRule = SymbolRuleBuilder()
			.symbol(squareClosingBracket)
			.ignoringAdjusting(' ', ',')
			.fromRight().shouldNotHaveNeighbor(squareOpeningBracket)
			.called("Неправильное оформление нескольких ссылок")
			.getRule()

		val bracket = '('

		val bracketRule = SymbolRuleBuilder()
			.symbol(bracket)
			.ignoringAdjusting(' ')
			.fromRight().shouldNotHaveNeighbor(*rusCapitalLetters.toCharArray())
			.called("Большая русская буква после скобки")

		val closingQuote = '”'
		val openingQuote = '“'

		val closingQuoteRule = SymbolRuleBuilder()
			.symbol(closingQuote)
			.ignoringEveryCharacterExcept(*"$closingQuote$openingQuote".toCharArray())
			.fromLeft().shouldHaveNeighbor(openingQuote)
			.inNeighborhood(20)
			.called("Неправильное использование закрывающей кавычки")
			.getRule()

		val openingQuoteRule = SymbolRuleBuilder()
			.symbol(openingQuote)
			.ignoringEveryCharacterExcept(*"$closingQuote$openingQuote".toCharArray())
			.fromRight().shouldHaveNeighbor(closingQuote)
			.inNeighborhood(20)
			.called("Неправильное использование открывающей кавычки")
			.getRule()

		val listRule = ListRuleBuilder()
			.inArea(NOWHERE.except(TABLE_OF_CONTENT))
			//.called("Only 1 subsection in a section")
			.called("Одна подсекция в секции")
			.disallow {
				if (it.nodes.count() == 1) it.nodes.first().getText() else listOf()
			}.getRule()

		val tableOfContentRule = TableOfContentRuleBuilder()
			.disallow {
				it.filter {
					// println("${it.text.count()} -> ${it.content}")
					val text = it.text.filter { it.text.trim().isNotEmpty() }
					((text.count() == 3 && (text[1].text == "Введение" || text[1].text == "Заключение")) ||
							(text.count() == 4 && text[1].text == "Список" && text[2].text == "литературы"))
				}
			}.called("Введение, заключение и список литературы не нумеруются")
			.getRule()

		return listOf(
			litlinkRule,
			shortDashRule,
			mediumDashRule,
			longDashRule,
			closingQuoteRule,
			openingQuoteRule,
			listRule,
			tableOfContentRule,
			bracketRule,
			multiLinksRule
		).map {
			it.process(document)
		}.flatten().toSet().toList()
	}
}
