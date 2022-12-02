package com.github.darderion.mundaneassignmentpolice.rules

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.and
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.or
import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.symbolbuilder.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import java.util.*

private val enLetters = "abcdefghijklmnopqrstuvwxyz"
private val enCapitalLetters = enLetters.uppercase(Locale.getDefault())
private val EN = enLetters + enCapitalLetters

private val rusLetters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
private val rusCapitalLetters = rusLetters.uppercase(Locale.getDefault())
private val RU = rusLetters + rusCapitalLetters

private val numbers = "0123456789"

val RULE_LITLINK = SymbolRuleBuilder<SymbolRuleBuilder<*>>()
	.symbol('?')
	.ignoringAdjusting(*" ,$numbers".toCharArray())
	.shouldNotHaveNeighbor(*"[]".toCharArray())
	//.called("Symbol '?' in litlink")
	.called("Символ ? в ссылке на литературу")
	.getRule()

val shortDash = '-'

// 3-way road
// one-sided battle

val shortDashRules = SymbolRuleBuilder<SymbolRuleBuilder<*>>()
	.symbol(shortDash)
	.shouldHaveNeighbor(*EN.toCharArray())
	.shouldHaveNeighbor(*RU.toCharArray())
	.shouldHaveNeighbor(*numbers.toCharArray())
	//.called("Incorrect usage of '-' symbol")
	.called("Неправильное использование дефиса")
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY, PDFArea.FOOTNOTE, PDFArea.TITLE_PAGE))

val RULE_SHORT_DASH = shortDashRules.getRule() and (
		shortDashRules.fromLeft().shouldHaveNeighbor('.')
			.shouldNotHaveNeighbor(*numbers.toCharArray()).getRule() or
				shortDashRules.fromRight().shouldHaveNeighbor('\n')
					.shouldNotHaveNeighbor(*numbers.toCharArray()).getRule()
		)

val mediumDash = '–'

val RULE_MEDIUM_DASH = SymbolRuleBuilder<SymbolRuleBuilder<*>>()
	.symbol(mediumDash)
	.ignoringIfIndex(0)
	.shouldHaveNeighbor(*numbers.toCharArray())
	//.called("Incorrect usage of '--' symbol")
	.called("Неправильное использование короткого тире")
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY, PDFArea.FOOTNOTE))
	.getRule()

val longDash = '—'

val RULE_LONG_DASH = SymbolRuleBuilder<SymbolRuleBuilder<*>>()
	.symbol(longDash)
	.ignoringAdjusting(' ')
	.shouldNotHaveNeighbor(*numbers.toCharArray())
	//.called("Incorrect usage of '---' symbol")
	.called("Неправильное использование длинного тире")
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY, PDFArea.FOOTNOTE))
	.getRule() and SymbolRuleBuilder<SymbolRuleBuilder<*>>()
	.symbol(longDash)
	.shouldHaveNeighbor(' ')
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY, PDFArea.FOOTNOTE))
	.getRule()

val closingQuote = '”'
val openingQuote = '“'

val RULE_CLOSING_QUOTATION = SymbolRuleBuilder<SymbolRuleBuilder<*>>()
	.symbol(closingQuote)
	.ignoringEveryCharacterExcept(*"$closingQuote$openingQuote".toCharArray())
	.fromLeft().shouldHaveNeighbor(openingQuote)
	.inNeighborhood(20)
	.called("Неправильное использование закрывающей кавычки")
	.getRule()

val RULE_OPENING_QUOTATION = SymbolRuleBuilder<SymbolRuleBuilder<*>>()
	.symbol(openingQuote)
	.ignoringEveryCharacterExcept(*"$closingQuote$openingQuote".toCharArray())
	.fromRight().shouldHaveNeighbor(closingQuote)
	.inNeighborhood(20)
	.called("Неправильное использование открывающей кавычки")
	.getRule()

const val squareClosingBracket = ']'
const val squareOpeningBracket = '['

val RULE_MULTIPLE_LITLINKS = SymbolRuleBuilder<SymbolRuleBuilder<*>>()
	.symbol(squareClosingBracket)
	.ignoringAdjusting(' ', ',')
	.fromRight().shouldNotHaveNeighbor(squareOpeningBracket)
	.called("Неправильное оформление нескольких ссылок")
	.getRule()

const val bracket = '('

val RULE_BRACKETS_LETTERS = List(2) {
	SymbolRuleBuilder<SymbolRuleBuilder<*>>()
		.symbol(bracket)
		.ignoringAdjusting(' ')
		.called("Большая русская буква после скобки")
		.type(RuleViolationType.Warning)
}.apply {
	first()
		.fromLeft()
		.shouldNotHaveNeighbor('.')
	last()
		.fromRight()
		.shouldNotHaveNeighbor(*rusCapitalLetters.toCharArray())
}.map { it.getRule() }
	.reduce { acc, symbolRule ->
		acc and symbolRule
	}

private const val openingBrackets = "([{<"
private const val closingBrackets = ")]}>"
private const val closingQuotes = "”»"
private const val punctuationSymbols = ".,;:!?"


private val spaceAroundBracketsRuleBuilders = List(2) { SymbolRuleBuilder<SymbolRuleBuilder<*>>() }
	.map { it.shouldHaveNeighbor(' ', '\n') }
	.map { it.called("Отсутствует пробел с внешней стороны скобок") }
	.apply {
		// setting up a rule that should look for a space before opening brackets
		first().fromLeft().ignoringAdjusting(*openingBrackets.toCharArray())
		// and this rule should look for after closing brackets
		last().fromRight()
			.ignoringAdjusting(*"$punctuationSymbols$closingQuotes$closingBrackets".toCharArray())
	}

// For case when round brackets are empty: "function()"
private val openingRoundBracketExceptionalRule = SymbolRuleBuilder<SymbolRuleBuilder<*>>()
	.symbol('(')
	.fromRight().shouldHaveNeighbor(')')
	.getRule()

val RULES_SPACE_AROUND_BRACKETS = spaceAroundBracketsRuleBuilders
	.zip(listOf(openingBrackets, closingBrackets).map { it.toCharArray() })
	.map { pair -> pair.second.map { pair.first.symbol(it).getRule() } }
	.flatten()
	.map {
		if (it.symbol == '(') it or openingRoundBracketExceptionalRule
		else it
	}

val RULE_CITATION = SymbolRuleBuilder<SymbolRuleBuilder<*>>()
	.symbol('[')
	.called("Некорректное цитирование")
	.inArea(PDFArea.SECTION)
	.ignoringAdjusting(' ', '\n')
	.fromLeft().shouldNotHaveNeighbor('.')
	.getRule()

