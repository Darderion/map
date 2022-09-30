package com.github.darderion.mundaneassignmentpolice.rules

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.Section
import com.github.darderion.mundaneassignmentpolice.checker.rule.list.ListRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.regex.RegexRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.sentence.SentenceRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.sentence.splitIntoSentences
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.and
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.or
import com.github.darderion.mundaneassignmentpolice.checker.rule.tableofcontent.TableOfContentRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.url.URLRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.url.then
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.WordRule
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.WordRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.or
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.utils.InvalidOperationException
import com.github.darderion.mundaneassignmentpolice.utils.LowQualityConferencesUtil
import com.github.darderion.mundaneassignmentpolice.utils.URLUtil
import java.util.*

private val enLetters = "abcdefghijklmnopqrstuvwxyz"
private val enCapitalLetters = enLetters.uppercase(Locale.getDefault())
private val EN = enLetters + enCapitalLetters

private val rusLetters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
private val rusCapitalLetters = rusLetters.uppercase(Locale.getDefault())
private val RU = rusLetters + rusCapitalLetters

private val numbers = "0123456789"

val RULE_LITLINK = SymbolRuleBuilder()
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

val RULE_MEDIUM_DASH = SymbolRuleBuilder()
	.symbol(mediumDash)
	.shouldHaveNeighbor(*numbers.toCharArray())
	//.called("Incorrect usage of '--' symbol")
	.called("Неправильное использование короткого тире")
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY, PDFArea.FOOTNOTE))
	.ignoringIfIndex(0)
	.getRule()

val longDash = '—'

val RULE_LONG_DASH = SymbolRuleBuilder()
	.symbol(longDash)
	.ignoringAdjusting(' ')
	.shouldNotHaveNeighbor(*numbers.toCharArray())
	//.called("Incorrect usage of '---' symbol")
	.called("Неправильное использование длинного тире")
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY, PDFArea.FOOTNOTE))
	.getRule() and SymbolRuleBuilder()
	.symbol(longDash)
	.shouldHaveNeighbor(' ')
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY, PDFArea.FOOTNOTE))
	.getRule()

val closingQuote = '”'
val openingQuote = '“'

val RULE_CLOSING_QUOTATION = SymbolRuleBuilder()
	.symbol(closingQuote)
	.ignoringEveryCharacterExcept(*"$closingQuote$openingQuote".toCharArray())
	.fromLeft().shouldHaveNeighbor(openingQuote)
	.inNeighborhood(20)
	.called("Неправильное использование закрывающей кавычки")
	.getRule()

val RULE_OPENING_QUOTATION = SymbolRuleBuilder()
	.symbol(openingQuote)
	.ignoringEveryCharacterExcept(*"$closingQuote$openingQuote".toCharArray())
	.fromRight().shouldHaveNeighbor(closingQuote)
	.inNeighborhood(20)
	.called("Неправильное использование открывающей кавычки")
	.getRule()

const val squareClosingBracket = ']'
const val squareOpeningBracket = '['

val RULE_MULTIPLE_LITLINKS = SymbolRuleBuilder()
	.symbol(squareClosingBracket)
	.ignoringAdjusting(' ', ',')
	.fromRight().shouldNotHaveNeighbor(squareOpeningBracket)
	.called("Неправильное оформление нескольких ссылок")
	.getRule()

const val bracket = '('

val RULE_BRACKETS_LETTERS = List(2) {
	SymbolRuleBuilder()
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


val RULE_NO_SPACE_AFTER_PUNCTUATION =  SymbolRuleBuilder()
		.symbol(',')
		.fromRight()
		.shouldHaveNeighbor(' ','\n')
		.called("Отсутствует пробел после запятой")
		.inArea(PDFRegion.NOWHERE.except(PDFArea.SECTION))
		.ignoringAdjusting(*numbers.toCharArray())
		.getRule()

private const val openingBrackets = "([{<"
private const val closingBrackets = ")]}>"
private const val closingQuotes = "”»"
private const val openingQuotes = "“«"
private const val punctuationSymbols = ".,;:!?"


private const val dotAndComma = ".,"

val RULE_SPACE_BEFORE_PUNCTUATION = List(dotAndComma.length) { SymbolRuleBuilder() }
		.mapIndexed {index, it ->
			it.symbol(punctuationSymbols[index])
					.fromLeft().shouldNotHaveNeighbor(' ','\n')
					.called("Используется пробел перед точкой или запятой")
					.inArea(PDFRegion.NOWHERE.except(PDFArea.SECTION))
					.getRule()
		}

private val spaceAroundBracketsRuleBuilders = List(2) { SymbolRuleBuilder() }
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
private val openingRoundBracketExceptionalRule = SymbolRuleBuilder()
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

val RULE_CITATION = SymbolRuleBuilder()
	.symbol('[')
	.ignoringAdjusting(' ', '\n')
	.fromLeft().shouldNotHaveNeighbor('.')
	.called("Некорректное цитирование")
	.inArea(PDFArea.SECTION)
	.getRule()

const val maxSentenceLength = 30
val RULE_LONG_SENTENCE = SentenceRuleBuilder()
		.called("Длинное предложение")
		.disallow { lines ->
			val results = mutableListOf<Line>()
			splitIntoSentences(lines).forEach { sentence ->
				var size = 0
				var isQuote = false
				sentence.forEachIndexed { index, word ->
					if (word.text.contains(Regex("[$openingQuotes]")) && index > 0 &&
							sentence[index-1].text.contains(Regex("[$punctuationSymbols:]")))
						isQuote = true
					if (word.text.contains(Regex("[$punctuationSymbols$longDash]")) && index > 0 &&
							sentence[index-1].text.contains(Regex("[$closingQuotes]")))
						isQuote = false
					if (!isQuote) size += 1
				}
				if (size > maxSentenceLength) results.addAll(lines)
			}
			results.toList()
		}.getRule()



val RULE_SINGLE_SUBSECTION = ListRuleBuilder()
	.inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
	//.called("Only 1 subsection in a section")
	.called("Одна подсекция в секции")
	.disallow {
		if (it.nodes.count() == 1) it.nodes.first().getText() else listOf()
	}.getRule()

val RULE_TABLE_OF_CONTENT_NUMBERS = TableOfContentRuleBuilder()
	.disallow {
		it.filter {
			// println("${it.text.count()} -> ${it.content}")
			val text = it.text.filter { it.text.trim().isNotEmpty() }
			((text.count() == 3 && (text[1].text == Section.INTRODUCTION.title ||
					text[1].text == Section.CONCLUSION.title)) ||
					(text.count() == 4 && (text[1].text + " " + text[2].text) == Section.BIBLIOGRAPHY.title))
		}
	}.called("Введение, заключение и список литературы не нумеруются")
	.getRule()

val RULE_SYMBOLS_IN_SECTION_NAMES = TableOfContentRuleBuilder()
	.disallow { listOfLines ->
		listOfLines.filter { line ->
			val text = line.text.filterNot { it.text == "." }           // remove leaders
				.filterNot { it.text.contains("[0-9]+\\.".toRegex()) }  // remove numbering
				.joinToString("")
			text.contains("[:.,]".toRegex())
		}
	}.called("""Символы ":", ".", "," в названии секции""")
	.getRule()

val sectionsThatMayPrecedeThis = mapOf<String, HashSet<String>>(
	Section.INTRODUCTION.title to hashSetOf(""),
	Section.PROBLEM_STATEMENT.title to hashSetOf(Section.INTRODUCTION.title),
	Section.REVIEW.title to hashSetOf(Section.PROBLEM_STATEMENT.title),
	Section.CONTENT.title to hashSetOf(Section.REVIEW.title, Section.CONTENT.title),
	Section.CONCLUSION.title to hashSetOf(Section.CONTENT.title),
	Section.BIBLIOGRAPHY.title to hashSetOf(Section.CONCLUSION.title)
)

val RULE_SECTIONS_ORDER = TableOfContentRuleBuilder()
	.disallow { listOfLines ->
		var nameOfPreviousSection = ""
		listOfLines
			.filterNot { line ->
				val words = line.text
					.filter { it.text.trim().isNotEmpty() }
					.filterNot { it.text.contains("[0-9]+\\.".toRegex()) }		// remove numbering
				words.isEmpty() || words[0].text == Section.TABLE_OF_CONTENT.title
			}
			.filter { line ->
				val words = line.text
					.filter { it.text.trim().isNotEmpty() }
					.filterNot { it.text.contains("[0-9]+\\.".toRegex()) }		// remove numbering

				val sectionName =
					if ((words[0].text + " " + words[1].text) == Section.BIBLIOGRAPHY.title ||
						(words[0].text + " " + words[1].text) == Section.PROBLEM_STATEMENT.title
					)
						words[0].text + " " + words[1].text
					else if (sectionsThatMayPrecedeThis.contains(words[0].text))
						words[0].text
					else
						Section.CONTENT.title

				val isRuleViolation =
					!sectionsThatMayPrecedeThis[sectionName]!!.contains(nameOfPreviousSection)
				nameOfPreviousSection = sectionName
				isRuleViolation
			}
	}
	.called("Неверный порядок секций")
	.getRule()

val smallNumbersRuleName = "Неправильное написание целых чисел от 1 до 9"
val smallNumbersRuleArea =
	PDFRegion.EVERYWHERE.except(PDFArea.PAGE_INDEX, PDFArea.TABLE_OF_CONTENT, PDFArea.BIBLIOGRAPHY)
val allowedWordsOnLeft = arrayOf(
	Regex("""[Рр]ис[a-я]*"""),
	Regex("""[Тт]абл[a-я]*"""), Regex("""[Сс]х[a-я]*"""),
	Regex("""[Dd]ef[a-z]*"""), Regex("""[Оо]пр[а-я]*"""),
	Regex("""[Tt]h[a-z]*"""), Regex("""[Тт]еорема""")
)
val allowedWordsOnRight = arrayOf(
	Regex("""[Gg][Bb]"""), Regex("""[Гг][Бб]"""),
	Regex("""[Mm][Bb]"""), Regex("""[Мм][Бб]"""),
	Regex("""[Gg][Hh][Zz]"""), Regex("""[Гг][Цц]"""),
	Regex("""→""")
)

val smallNumbersRuleBuilder1 = WordRuleBuilder()		//for nearest words
	.called(smallNumbersRuleName)
	.inArea(smallNumbersRuleArea)
	.ignoringAdjusting(Regex("""\s"""), Regex("""\."""))
	.ignoringIfIndex(0)

val smallNumbersRuleBuilder2 = WordRuleBuilder()		//for decimal fractions and version numbers
	.called(smallNumbersRuleName)
	.inArea(smallNumbersRuleArea)
	.shouldHaveNeighbor(Regex("""\."""), Regex(""","""),
		Regex("""[0-9]+"""))
	.shouldHaveNumberOfNeighbors(2)

val smallNumbersRuleBuilder3 = WordRuleBuilder()		//for links
	.called(smallNumbersRuleName)
	.inArea(smallNumbersRuleArea)
	.fromLeft()
	.ignoringWords(true)
	.ignoringAdjusting(Regex(""","""), Regex("""\s"""))
	.shouldHaveNeighbor(Regex("""\["""))

val RULES_SMALL_NUMBERS = List<WordRule>(9) { index ->
	smallNumbersRuleBuilder1.word((index + 1).toString())
		.fromLeft().shouldHaveNeighbor(*allowedWordsOnLeft).getRule() or
	smallNumbersRuleBuilder1.word((index + 1).toString())
		.fromRight().shouldHaveNeighbor(*allowedWordsOnRight).getRule() or
	smallNumbersRuleBuilder2.word((index + 1).toString()).fromLeft().getRule() or
	smallNumbersRuleBuilder2.fromRight().getRule() or
	smallNumbersRuleBuilder3.word((index + 1).toString()).getRule()
}

const val shortenedUrlRuleName = "Сокращённая ссылка"
val shortenedUrlRuleArea = PDFRegion.NOWHERE.except(PDFArea.FOOTNOTE, PDFArea.BIBLIOGRAPHY)

val urlShortenersListRule = URLRuleBuilder()
	.called(shortenedUrlRuleName)
	.inArea(shortenedUrlRuleArea)
	.type(RuleViolationType.Error)
	.disallow { urls ->
		val urlShorteners = URLUtil.getUrlShorteners()
		urls.filter { url ->
			urlShorteners.any { URLUtil.equalDomainName(it, url.text) }
		}.map { it to it.lines }
	}.getRule()

val shortUrlRule = URLRuleBuilder()
	.called(shortenedUrlRuleName)
	.inArea(shortenedUrlRuleArea)
	.type(RuleViolationType.Warning)
	.disallow { urls ->
		urls.filter { url ->
			URLUtil.getDomainName(url.text).replace(".", "").length in (3..5)
		}.map { it to it.lines }
	}.getRule()

val allowedDomainNamesWithRedirect = listOf("doi.org", "dx.doi.org")

val urlWithRedirectRule = URLRuleBuilder()
	.called(shortenedUrlRuleName)
	.inArea(shortenedUrlRuleArea)
	.type(RuleViolationType.Warning)
	.disallow { urls ->
		urls.filterNot { url ->
			allowedDomainNamesWithRedirect.any { URLUtil.equalDomainName(it, url.text) }
		}.filter { url ->
			try {
				URLUtil.isRedirect(url.text)
			} catch (_: InvalidOperationException) {
				false
			}
		}.map { it to it.lines }
	}.getRule()

val RULE_SHORTENED_URLS = urlShortenersListRule then shortUrlRule then urlWithRedirectRule

val RULE_URLS_UNIFORMITY = URLRuleBuilder()
	.called("Ссылки разных видов")
	.inArea(PDFRegion.NOWHERE.except(PDFArea.FOOTNOTE, PDFArea.BIBLIOGRAPHY))
	.disallow { urls ->
		var filteredUrls = urls.filter { url ->
			!url.text.startsWith("https://www")
		}
		if (urls.size == filteredUrls.size) {
			filteredUrls = filteredUrls.filter { url ->
				!url.text.startsWith("www")
			}
			if (urls.size == filteredUrls.size) {
				filteredUrls = filteredUrls.filter { url ->
					!url.text.startsWith("htt")
				}
			}
		}
		filteredUrls.map { it to it.lines }
	}.getRule()

val RULE_ORDER_OF_REFERENCES = RegexRuleBuilder()
	.called("Неверный порядок ссылок на литературу")
	.regex(Regex("""\[[0-9,\s]+\]"""))
	.searchIn(1)
	.disallow { matches ->
		matches.filter { pair ->
			val references = pair.first
			val referencesInIntList = references
				.slice(IntRange(1, references.length - 2))
				.split(Regex(""","""))
				.map { it.trim() }
				.filter { it.isNotEmpty() }
				.map { it.toInt() }
			referencesInIntList != referencesInIntList.sorted()
		}.map { it.second }
	}.getRule()

val RULE_VARIOUS_ABBREVIATIONS = RegexRuleBuilder()
	.called("Использованы различные версии сокращения")
	.regex(Regex("""[a-zA-Zа-яА-Я]+"""))
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY))
	.disallow { matches ->
		val abbreviations = hashSetOf<String>()
		val allWords = hashMapOf<String, HashSet<String>>()
		matches.forEach { pair ->
			val word = pair.first
			if (word.slice(IntRange(1, word.length - 1))
					.count { it.isUpperCase() } > 0)
				abbreviations.add(word.uppercase())
			if (!allWords.containsKey(word.lowercase()))
				allWords.put(word.lowercase(), hashSetOf())
			allWords[word.lowercase()]?.add(word)
		}
		matches.filter { pair ->
			val word = pair.first
			if (abbreviations.contains(word.uppercase()))
				allWords[word.lowercase()]?.size!! > 1
			else
				false
		}.map { it.second }
	}.getRule()

val RULE_LOW_QUALITY_CONFERENCES = URLRuleBuilder()
	.called("Ссылка на низкокачественную конференцию")
	.inArea(PDFArea.BIBLIOGRAPHY)
	.disallow { urls ->
		val lowQualityConferencesList = LowQualityConferencesUtil.getList()
			.map {
				it.removePrefix("http://").removePrefix("https://")
			}
		urls.filter { url ->
			lowQualityConferencesList
				.any { conference -> url.text.contains(conference) }
		}.map { it to it.lines }
	}.getRule()
