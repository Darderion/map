package com.github.darderion.mundaneassignmentpolice.rules

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.SectionName
import com.github.darderion.mundaneassignmentpolice.checker.rule.list.ListRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.regex.RegexRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.section.SectionSizeRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.sentence.SentenceRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.sentence.splitIntoSentences
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.and
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.or
import com.github.darderion.mundaneassignmentpolice.checker.rule.line.LineRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.url.URLRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.*
import com.github.darderion.mundaneassignmentpolice.checker.rule.url.then
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.WordRule
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.WordRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.or
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.splitToWordsAndPunctuations
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.utils.InvalidOperationException
import com.github.darderion.mundaneassignmentpolice.utils.LowQualityConferencesUtil
import com.github.darderion.mundaneassignmentpolice.utils.ResourcesUtil
import com.github.darderion.mundaneassignmentpolice.utils.URLUtil
import java.util.*

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody

private val enLetters = "abcdefghijklmnopqrstuvwxyz"
private val enCapitalLetters = enLetters.uppercase(Locale.getDefault())
private val EN = enLetters + enCapitalLetters

private val rusLetters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
private val rusCapitalLetters = rusLetters.uppercase(Locale.getDefault())
private val RU = rusLetters + rusCapitalLetters

private val numbers = "0123456789"
val microservice_url = "http://127.0.0.1:8084/predict"

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

private const val openingBrackets = "([{<"
private const val closingBrackets = ")]}>"
private const val closingQuotes = "”»"
private const val punctuationSymbols = ".,;:!?"

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

val RULE_SECTION_NUMBERING_FROM_0 = LineRuleBuilder()
	.inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
		.disallow { listOfLines ->
			listOfLines.filter { line ->
				val text = line.text
						.filter { it.text.contains("([0-9])*+\\.".toRegex()) }.joinToString("")
				text.contains("\\.0\\.".toRegex() ) || text.isNotEmpty() && text.first()=='0' // detect .0. or 0. (not 10.0)
			}
		}.called("Нумерация секций не должна начинаться с нуля")
		.getRule()

val RULE_SINGLE_SUBSECTION = ListRuleBuilder()
	.inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
	//.called("Only 1 subsection in a section")
	.called("Одна подсекция в секции")
	.disallow {
		if (it.nodes.count() == 1) it.nodes.first().getText() else listOf()
	}.getRule()

val RULE_TABLE_OF_CONTENT_NUMBERS = LineRuleBuilder()
	.inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
	.disallow {
		it.filter {
			// println("${it.text.count()} -> ${it.content}")
			val text = it.text.filter { it.text.trim().isNotEmpty() }
			((text.count() == 3 && (text[1].text == SectionName.INTRODUCTION.title ||
					text[1].text == SectionName.CONCLUSION.title)) ||
					(text.count() == 4 && (text[1].text + " " + text[2].text) == SectionName.BIBLIOGRAPHY.title))
		}
	}.called("Введение, заключение и список литературы не нумеруются")
	.getRule()

val RULE_SYMBOLS_IN_SECTION_NAMES = LineRuleBuilder()
	.inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
	.disallow { listOfLines ->
		listOfLines.filter { line ->
			val text = line.text.filterNot { it.text == "." }           // remove leaders
				.filterNot { it.text.contains("[0-9]+\\.".toRegex()) }  // remove numbering
				.joinToString("")
			text.contains("[:.,]".toRegex())
		}
	}.called("""Символы ":", ".", "," в названии секции""")
	.getRule()

val RULE_TWO_IDENTICAL_WORDS = PredicateWordRuleBuilder()
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.TABLE_OF_CONTENT))
	.called("Два одинаковых слова подряд")
	.setRuleBody { document: PDFDocument, line: Int, index: Int ->
		val area = PDFRegion.EVERYWHERE.except(PDFArea.TABLE_OF_CONTENT)
		val wordIndex = index + splitToWordsAndPunctuations(
			document.getTextFromLines(line - 1, line - 1, area)
		).size +
				2 * line.coerceAtMost(1).coerceAtMost(1)
		val words = splitToWordsAndPunctuations(
			document.getTextFromLines(line - 1, line + 1, area)
		)
		val sideWords = mutableListOf(
			words.slice(IntRange(0, wordIndex - 1)).reversed(),
			words.slice(IntRange(wordIndex + 1, words.size - 1))
		)
		val neighbors = sideWords
			.filter { it.isNotEmpty() }
			.map { it.slice(IntRange(0, 0)) }
			.flatten()
			.map { it.lowercase(Locale.getDefault()) }

		return@setRuleBody neighbors[0] == neighbors[1] && neighbors[0].first().isLetter()
	}
	.getRule()

val sectionsThatMayPrecedeThis = mapOf<String, HashSet<String>>(
	SectionName.INTRODUCTION.title to hashSetOf(""),
	SectionName.PROBLEM_STATEMENT.title to hashSetOf(SectionName.INTRODUCTION.title),
	SectionName.REVIEW.title to hashSetOf(SectionName.PROBLEM_STATEMENT.title),
	SectionName.CONTENT.title to hashSetOf(SectionName.REVIEW.title, SectionName.CONTENT.title),
	SectionName.CONCLUSION.title to hashSetOf(SectionName.CONTENT.title),
	SectionName.BIBLIOGRAPHY.title to hashSetOf(SectionName.CONCLUSION.title)
)

val RULE_SECTIONS_ORDER = LineRuleBuilder()
	.inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
	.disallow { listOfLines ->
		var nameOfPreviousSection = ""
		listOfLines
			.filterNot { line ->
				val words = line.text
					.filter { it.text.trim().isNotEmpty() }
					.filterNot { it.text.contains("[0-9]+\\.".toRegex()) }		// remove numbering
				words.isEmpty() || words[0].text == SectionName.TABLE_OF_CONTENT.title
			}
			.filter { line ->
				val words = line.text
					.filter { it.text.trim().isNotEmpty() }
					.filterNot { it.text.contains("[0-9]+\\.".toRegex()) }		// remove numbering

				val sectionName =
					if ((words[0].text + " " + words[1].text) == SectionName.BIBLIOGRAPHY.title ||
						(words[0].text + " " + words[1].text) == SectionName.PROBLEM_STATEMENT.title
					)
						words[0].text + " " + words[1].text
					else if (sectionsThatMayPrecedeThis.contains(words[0].text))
						words[0].text
					else
						SectionName.CONTENT.title

				val isRuleViolation =
					!sectionsThatMayPrecedeThis[sectionName]!!.contains(nameOfPreviousSection)
				nameOfPreviousSection = sectionName
				isRuleViolation
			}
	}
	.called("Неверный порядок секций")
	.getRule()

val RULE_UNSCIENTIFIC_SENTENCE = SentenceRuleBuilder()
		.called("Ненаучный стиль")
		.disallow { lines ->
			val results = mutableListOf<Line>()
			splitIntoSentences(lines).forEach { sentence ->
				val body = "{ \"data\" : \"${sentence.joinToString(separator = " ")}\" }"

				val (_, _, result) = Fuel.post(microservice_url)
						.jsonBody(body)
						.responseString()
				result.fold(success = {
					if ("unscientific" in it.toString()) {
						results.addAll(lines)
					}

				}, failure = {
					println(String(it.errorData))
				})

			}
			results.toList()
		}.getRule()

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

private const val sectionSizeRuleName = "Слишком длинная секция"

val introductionAndConclusionSizeRuleError = SectionSizeRuleBuilder()
	.called(sectionSizeRuleName)
	.sections(SectionName.INTRODUCTION, SectionName.CONCLUSION)
	.shouldBeLessThan()
	.limitByPages(4)
	.type(RuleViolationType.Error)
	.getRule()

val introductionAndConclusionSizeRuleWarning = SectionSizeRuleBuilder()
	.called(sectionSizeRuleName)
	.sections(SectionName.INTRODUCTION, SectionName.CONCLUSION)
	.shouldNotBeEqual()
	.limitByPages(3)
	.type(RuleViolationType.Warning)
	.getRule()

val sectionsSizeRule = SectionSizeRuleBuilder()
	.called(sectionSizeRuleName)
	.sections(SectionName.PROBLEM_STATEMENT, SectionName.REVIEW, SectionName.CONTENT, SectionName.BIBLIOGRAPHY)
	.shouldNotBeGreaterThan()
	.limitByPercentage(50)
	.getRule()

val RULES_SECTION_SIZE = listOf(
	introductionAndConclusionSizeRuleError,
	introductionAndConclusionSizeRuleWarning,
	sectionsSizeRule
)

val ruleShortenedURLBuilder = URLRuleBuilder()
	.called("Сокращённая ссылка")
	.inArea(PDFRegion.NOWHERE.except(PDFArea.FOOTNOTE, PDFArea.BIBLIOGRAPHY))

val RULE_DISALLOWED_WORDS = WordRuleBuilder()
		.called("слова \"theorem, definition, lemma\" не должны использоваться")
		.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY,PDFArea.TITLE_PAGE))
		.fromLeft()
		.ignoringPunctuation(true)
		.shouldNotHaveNeighbor(
				Regex("""[Tt]heorem"""),
				Regex("""[Dd]efinition"""),
				Regex("""[Ll]emma"""))
		.getRule()

val RULE_INCORRECT_ABBREVIATION = WordRuleBuilder()
		.called("Неправильное написание аббревиатуры \"вуз\"")
		.inArea(PDFRegion.EVERYWHERE)
		.ignoringPunctuation(true)
		.shouldNotHaveNeighbor(
				Regex("""ВУЗ(\p{Pd})?(.*)""") // detect "ВУЗ-", "ВУЗ", not "вуз","Вуз"
		)
		.getRule()

const val shortenedUrlRuleName = "Сокращённая ссылка"
val shortenedUrlRuleArea = PDFRegion.NOWHERE.except(PDFArea.FOOTNOTE, PDFArea.BIBLIOGRAPHY)

val urlShortenersListRule = URLRuleBuilder()
	.called(shortenedUrlRuleName)
	.inArea(shortenedUrlRuleArea)
	.type(RuleViolationType.Error)
	.disallow { urls ->
		val urlShorteners = ResourcesUtil.getResourceLines("URLShorteners.txt")
		urls.filter { url ->
			urlShorteners.any { shortener -> URLUtil.equalDomainName(shortener, url.text) }
		}.map { it to it.lines }
	}.getRule()

val urlWithRedirectRule = URLRuleBuilder()
	.called(shortenedUrlRuleName)
	.inArea(shortenedUrlRuleArea)
	.type(RuleViolationType.Warning)
	.ignoreIf { url ->
		val allowedUrls = ResourcesUtil.getResourceLines("AllowedDomainsWithRedirect.txt")
		allowedUrls.any { allowedUrl -> URLUtil.equalDomainName(allowedUrl, url.text) }
	}
	.ignoreIf { url ->
		// Remain only URLs (potential shortened URLs) that have a domain name no longer 5 characters and
		// only one part after a domain (token in shortened URL) that is less than 10 characters.
		val partsAfterDomain = URLUtil.partAfterDomain(url.text).split('/').filter { it.isNotEmpty() }
		URLUtil.getDomainName(url.text).length > 5 ||
			partsAfterDomain.isEmpty() ||
			partsAfterDomain.size > 1 ||
			partsAfterDomain.first().length >= 10
	}
	.disallow { urls ->
		urls.filter { url ->
			try {
				URLUtil.isRedirect(url.text)
			} catch (_: InvalidOperationException) {
				false
			}
		}.map { it to it.lines }
	}.getRule()

val RULE_SHORTENED_URLS = urlShortenersListRule then urlWithRedirectRule

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
	.regex(Regex("""\[[0-9,\-\s]+\]"""))
	.searchIn(1)
	.disallow { matches ->
		matches.filter { pair ->
			val references = pair.first
			val referencesInIntList = references
				.slice(IntRange(1, references.length - 2))
				.split(Regex("""[,\-]"""))
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
				it.removePrefix("http://")
					.removePrefix("https://")
					.removePrefix("www.")
					.removeSuffix("/")
			}
		urls.filter { url ->
			lowQualityConferencesList
				.any { conference -> url.text.contains(conference) }
		}.map { it to it.lines }
	}.getRule()

val fieldsCoordinateX = 560
val RULE_OUTSIDE_FIELDS = LineRuleBuilder()
	.called("Слово вышло  поля")
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY, PDFArea.FOOTNOTE, PDFArea.TITLE_PAGE))
	.disallow { it ->
		it.filter {
			it.lastPosition.x > fieldsCoordinateX
		}
	}
	.getRule()
