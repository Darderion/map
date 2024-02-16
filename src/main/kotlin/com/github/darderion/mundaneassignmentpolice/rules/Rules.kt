package com.github.darderion.mundaneassignmentpolice.rules

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.Section
import com.github.darderion.mundaneassignmentpolice.checker.rule.list.ListRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.regex.RegexRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.and
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.or
import com.github.darderion.mundaneassignmentpolice.checker.rule.LineRule.LineRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.sentence.SentenceRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.sentence.splitIntoSentences
import com.github.darderion.mundaneassignmentpolice.checker.rule.url.URLRuleBuilder
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
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import java.util.*


private val enLetters = "abcdefghijklmnopqrstuvwxyz"
private val enCapitalLetters = enLetters.uppercase(Locale.getDefault())
private val EN = enLetters + enCapitalLetters

private val rusLetters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
private val rusCapitalLetters = rusLetters.uppercase(Locale.getDefault())
private val RU = rusLetters + rusCapitalLetters
const val conclusionWord = "Заключение"
private val numbers = "0123456789"
val microservice_url = "http://127.0.0.1:8084/predict"
val RULE_LITLINK = SymbolRuleBuilder()
	.symbol('?')
	.ignoringAdjusting(*" ,$numbers".toCharArray())
	.shouldNotHaveNeighbor(*"[]".toCharArray())
	//.called("Symbol '?' in litlink")
	.called("Символ ? в ссылке на литературу")
	.setDescription("Дефис используется только для описания диапазонов. Например: 1941–1945 или стр. 25–45. Подробнее написано тут: https://www.vgsa.ru/blogs/mos/80/index.php?special=Y и https://ru.wikipedia.org/wiki/%D0%A2%D0%B8%D1%80%D0%B5#%D0%A1%D1%80%D0%B5%D0%B4%D0%BD%D0%B5%D0%B5_%D1%82%D0%B8%D1%80%D0%B5\"")
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
	.setDescription("Дефис используется только для описания диапазонов. Например: 1941–1945 или стр. 25–45. Подробнее написано тут: https://www.vgsa.ru/blogs/mos/80/index.php?special=Y и https://ru.wikipedia.org/wiki/%D0%A2%D0%B8%D1%80%D0%B5#%D0%A1%D1%80%D0%B5%D0%B4%D0%BD%D0%B5%D0%B5_%D1%82%D0%B8%D1%80%D0%B5")
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
	.setDescription("Короткое в русском языке называется 'дефис' и ставится между словами или для присоединения частиц (кое-кто, по-видимому, во-первых), в качестве знака сокращения (физ-ра, студ-т), используется как знак переноса, а также в словах, являющихся определениями или сложно-составных словах (попрыгунья-стрекоза, ковер-самолет). Подробнее написано тут: https://www.vgsa.ru/blogs/mos/80/index.php?special=Y и https://theoryandpractice.ru/posts/18471-dlinnoe-ili-korotkoe-kak-stavit-tire")
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
	.setDescription("В русском языке оно ставится вместо отсутствующего члена предложения, для указания маршрутов и в ряде других случаев. Подробнее написано тут https://www.vgsa.ru/blogs/mos/80/index.php?special=Y и https://theoryandpractice.ru/posts/18471-dlinnoe-ili-korotkoe-kak-stavit-tire")
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
	.setDescription("Дефис используется только для описания диапазонов. Например: 1941–1945 или стр. 25–45. Подробнее написано тут: https://www.vgsa.ru/blogs/mos/80/index.php?special=Y и https://ru.wikipedia.org/wiki/%D0%A2%D0%B8%D1%80%D0%B5#%D0%A1%D1%80%D0%B5%D0%B4%D0%BD%D0%B5%D0%B5_%D1%82%D0%B8%D1%80%D0%B5")
	.getRule()

val RULE_OPENING_QUOTATION = SymbolRuleBuilder()
	.symbol(openingQuote)
	.ignoringEveryCharacterExcept(*"$closingQuote$openingQuote".toCharArray())
	.fromRight().shouldHaveNeighbor(closingQuote)
	.inNeighborhood(20)
	.setDescription("Дефис используется только для описания диапазонов. Например: 1941–1945 или стр. 25–45. Подробнее написано тут: https://www.vgsa.ru/blogs/mos/80/index.php?special=Y и https://ru.wikipedia.org/wiki/%D0%A2%D0%B8%D1%80%D0%B5#%D0%A1%D1%80%D0%B5%D0%B4%D0%BD%D0%B5%D0%B5_%D1%82%D0%B8%D1%80%D0%B5")
	.called("Неправильное использование открывающей кавычки")
	.getRule()

const val squareClosingBracket = ']'
const val squareOpeningBracket = '['

val RULE_MULTIPLE_LITLINKS = SymbolRuleBuilder()
	.symbol(squareClosingBracket)
	.ignoringAdjusting(' ', ',')
	.fromRight().shouldNotHaveNeighbor(squareOpeningBracket)
	.called("Неправильное оформление нескольких ссылок")
	.setDescription("Несколько ссылок подряд следует оформлять так [0,1], а не [0] [1]")
	.getRule()

const val bracket = '('

val RULE_BRACKETS_LETTERS = List(2) {
	SymbolRuleBuilder()
		.symbol(bracket)
		.ignoringAdjusting(' ')
		.called("Большая русская буква после скобки")
		.setDescription("После открывающей круглой скобки следует ставить маленькую букву, если речь идет не о названиях и других сложных случаев")
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
	.map {it.setDescription("В тексте с внешней стороны скобки следует ставить пробел (вот так) вот. Если правило срабатывает в программном коде или в формулах — считать это ложным срабатыванием")}
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
	.setDescription("Не цитируйте в стиле википедии — после точки. Надо цитировать рядом с “действующим” словом, например “В работе [6] было показано ...”, “Авторы [8] утверждают ...”, “Было показано [2], что ...”. Либо, если очень надо то ставим ссылку в конце предложения и до точки.")
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
	.setDescription("Нумерация секций не должна начинаться с нуля")
		.getRule()

	val RULE_SINGLE_SUBSECTION = ListRuleBuilder()
	.inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
	//.called("Only 1 subsection in a section")
	.called("Одна подсекция в секции")
	.setDescription("В работе не должно быть одной подсекции в секции, необходимо перенести текст на уровень выше и переделать название")
	.disallowInSingleList {
			if (it.nodes.count() == 1) it.nodes.first().getText() else listOf()
		}.getRule()

val RULE_TABLE_OF_CONTENT_NUMBERS = LineRuleBuilder()
	.inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
	.disallow {
		it.filter {
			// println("${it.text.count()} -> ${it.content}")
			val text = it.text.filter { it.text.trim().isNotEmpty() }
			((text.count() == 3 && (text[1].text == Section.INTRODUCTION.title ||
					text[1].text == Section.CONCLUSION.title)) ||
					(text.count() == 4 && (text[1].text + " " + text[2].text) == Section.BIBLIOGRAPHY.title))
		}
	}.called("Введение, заключение и список литературы не нумеруются")
	.setDescription("Введение, заключение и список литературы не нумеруются")
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
	.setDescription("В общем случае наличие данных символов в названии секции является неуместным или прямо неправильным (например, когда люди ставят точку в конце названия секции или когда руками делают оглавление и ставят десятки точек, а потом номер страницы). Хотя если и исключения, вида “Pybind11 и Boost.Python”, “Обзор: современное состояние работ”")
	.getRule()

val sectionsThatMayPrecedeThis = mapOf<String, HashSet<String>>(
	Section.INTRODUCTION.title to hashSetOf(""),
	Section.PROBLEM_STATEMENT.title to hashSetOf(Section.INTRODUCTION.title),
	Section.REVIEW.title to hashSetOf(Section.PROBLEM_STATEMENT.title),
	Section.CONTENT.title to hashSetOf(Section.REVIEW.title, Section.CONTENT.title),
	Section.CONCLUSION.title to hashSetOf(Section.CONTENT.title),
	Section.BIBLIOGRAPHY.title to hashSetOf(Section.CONCLUSION.title)
)

val RULE_NO_TASKS = LineRuleBuilder()
	.inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
	.called("Задачи не выделены в содержании")
	.disallow {
		val tasks = it.filter { it.text.toString().contains("адач") }
		if (tasks.isEmpty()) listOf(it.first()) else listOf()
	}
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
val RULE_SECTIONS_ORDER = LineRuleBuilder()
	.inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
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
	.setDescription("Это весьма сложное и “хрупкое” правило, которое может давать ложные срабатывания. Предполагается что работа начнется с введения, потому будет постановка задачи, какой-то обзор, затем предлагаемое решение, потом эксперименты и заключение. Возможны и другие варианты, такие как обзор перед заключением.")
	.getRule()

val smallNumbersRuleName = "Неправильное написание целых чисел от 1 до 9"
val smallNumbersRuleArea =
	PDFRegion.EVERYWHERE.except(PDFArea.PAGE_INDEX, PDFArea.TABLE_OF_CONTENT, PDFArea.BIBLIOGRAPHY)
val allowedWordsOnLeft = arrayOf(
	Regex("""[Рр]ис[a-я]*"""),
	Regex("""[Тт]абл[a-я]*"""), Regex("""[Сс]х[a-я]*"""),
	Regex("""[Dd]ef[a-z]*"""), Regex("""[Оо]пр[а-я]*"""),
	Regex("""[Tt]h[a-z]*"""), Regex("""[Тт]еорем[а-я]*"""), Regex("""№"""),
	Regex("""[Дд]иаграм[а-я]*"""), Regex("""[Пп]ункт[а-я]*"""),Regex("""[Сс]тро[а-я]*"""),
	Regex("""[Ll]ist[a-z]*"""), Regex("""[Лл]истинг[а-я]*"""),
	Regex("""[Пп]риложе[а-я]*"""), Regex("""[Зз]начени[а-я]*"""),
	Regex("""[Гг]лав[а-я]*"""),Regex("""[Шш]аг[а-я]*"""),Regex("""[Зз]апро[а-я]*"""),
	Regex("""[Пп]риме[а-я]*"""),Regex("""[Ии]ллюстрац[а-я]*"""),
	Regex("""[Рр]азд[а-я]*"""), Regex("""[Нн]омер[а-я]*"""), Regex("""[Лл]емм[а-я]*"""),Regex(""" """)
	)
	
val newlineSplittingSymbols = arrayOf(Regex("""\n"""),Regex("""\s"""),Regex("""-"""))

val hyphenatedWordsOnLeft = arrayOf(
	arrayOf(Regex("""[Рр]и[а-я]*"""), *newlineSplittingSymbols, Regex("""н[а-я]*""")),
	arrayOf(Regex("""[Тт]аб[а-я]*"""), *newlineSplittingSymbols,Regex("""ц[а-я]*""")),
	arrayOf(Regex("""[Сс]хе"""), *newlineSplittingSymbols,Regex("""м[а-я]*""")),
	arrayOf(Regex("""[Оо]п[а-я]*"""), *newlineSplittingSymbols, Regex("""ни[а-я]*""")),
	arrayOf(Regex("""[Тт]ео[а-я]*"""), *newlineSplittingSymbols, Regex("""м[а-я]*""")),
	arrayOf(Regex("""[Дд]и[а-я]*"""), *newlineSplittingSymbols, Regex("""м[а-я]*""")),
	arrayOf(Regex("""[Пп]унк"""), *newlineSplittingSymbols, Regex("""т[а-я]*""")),
	arrayOf(Regex("""[Сс]тро"""), *newlineSplittingSymbols, Regex("""к[а-я]*""")),
	arrayOf(Regex("""[Лл]ис"""), *newlineSplittingSymbols, Regex("""г[а-я]*""")),
	arrayOf(Regex("""[Пп]ри[а-я]*"""), *newlineSplittingSymbols, Regex("""ни[а-я]*""")),
	arrayOf(Regex("""[Зз]на[а-я]*"""), *newlineSplittingSymbols, Regex("""ни[а-я]*""")),
	arrayOf(Regex("""[Гг]ла"""), *newlineSplittingSymbols, Regex("""в[а-я]*""")),
	arrayOf(Regex("""[Шш]а"""), *newlineSplittingSymbols, Regex("""г[а-я]*""")),
	arrayOf(Regex("""[Зз]а[а-я]*"""), *newlineSplittingSymbols, Regex("""c[а-я]*""")),
	arrayOf(Regex("""[Ии]л[а-я]*"""), *newlineSplittingSymbols, Regex("""ц[а-я]*""")),
	arrayOf(Regex("""[Рр]аз[а-я]*"""), *newlineSplittingSymbols, Regex("""л[а-я]*""")),
	arrayOf(Regex("""[Нн]о[а-я]*"""), *newlineSplittingSymbols, Regex("""р[а-я]*""")),
	arrayOf(Regex("""[Лл]ем"""), *newlineSplittingSymbols, Regex("""м[а-я]*"""))
)

val allowedWordsOnRight = arrayOf(
	Regex("""[Gg][Bb]"""), Regex("""[Гг][Бб]"""),
	Regex("""[Mm][Bb]"""), Regex("""[Мм][Бб]"""),
	Regex("""[Gg][Hh][Zz]"""), Regex("""[Гг][Гг][Цц]"""),
	Regex("""→"""), Regex("""[Кк]илобайт[а-я]*"""), Regex("""[Кк][Бб]"""),
	Regex("""[Kk][Bb]"""),Regex("""[Бб]айт[а-я]*"""), Regex("""[Kk]byte[s]"""),
	Regex("""[Bb]ytes"""), Regex("""[Бб]ит""")
)

val smallNumbersRuleBuilder1Left = WordRuleBuilder()        //for nearest words
	.called(smallNumbersRuleName)
	.inArea(smallNumbersRuleArea)
	.fromLeft()
	.ignoringAdjusting(Regex("""\."""))
	.shouldHaveNeighbor(*allowedWordsOnLeft)
	.shouldHaveNumberOfNeighbors(2)
	.setDescription("Целые числа от 1 до 9  в тексте работы следует заменять словами за исключением ряда случаев таких как: таблицы, даты, сравнение, формулы, код и др.")

val smallNumbersRuleBuilder1Right = WordRuleBuilder()       //as previous, but for right
	.called(smallNumbersRuleName)
	.inArea(smallNumbersRuleArea)
	.fromRight()
	.ignoringAdjusting(Regex("""\s"""), Regex("""\."""))
	.shouldHaveNeighbor(*allowedWordsOnRight)
	.ignoringIfIndex(0)
	.setDescription("Целые числа от 1 до 9  в тексте работы следует заменять словами за исключением ряда случаев таких как: таблицы, даты, сравнение, формулы, код и др.")

val smallNumbersRuleBuilder2 = WordRuleBuilder()        //for decimal fractions and version numbers
	.called(smallNumbersRuleName)
	.inArea(smallNumbersRuleArea)
	.shouldHaveNeighbor(
		Regex("""\."""), Regex(""","""),
		Regex("""[0-9]+""")
	)
	.shouldHaveNumberOfNeighbors(2)
	.setDescription("Целые числа от 1 до 9  в тексте работы следует заменять словами за исключением ряда случаев таких как: таблицы, даты, сравнение, формулы, код и др.")

val smallNumbersRuleBuilder3 = WordRuleBuilder()        //for links and operators
	.called(smallNumbersRuleName)
	.inArea(smallNumbersRuleArea)
	.ignoringAdjusting(Regex(""","""), Regex("""\s"""))
	.shouldHaveNeighbor(Regex("""\[|\]"""), Regex("""\/"""), Regex("""<|≤|>|≥|=|\+"""),
						Regex("""\""""))
	.setDescription("Целые числа от 1 до 9  в тексте работы следует заменять словами за исключением ряда случаев таких как: таблицы, даты, сравнение, формулы, код и др.")

val smallNumbersRuleBuilderHyphenated = WordRuleBuilder() //for hyphenated words
	.called(smallNumbersRuleName)
	.inArea(smallNumbersRuleArea)
	.fromLeft()
	.inNeighborhood(3)
	.shouldHaveNumberOfNeighbors(5)
	.setDescription("Целые числа от 1 до 9  в тексте работы следует заменять словами за исключением ряда случаев таких как: таблицы, даты, сравнение, формулы, код и др.")

val smallNumbersRuleBuilder4 = hyphenatedWordsOnLeft.map { hyphenatedWord ->
	smallNumbersRuleBuilderHyphenated.shouldHaveNeighbor(*hyphenatedWord)
}
	
val RULES_SMALL_NUMBERS = List<WordRule>(9) { index ->
	smallNumbersRuleBuilder1Left.word((index + 1).toString()).getRule() or
	smallNumbersRuleBuilder1Right.word((index + 1).toString()).getRule() or
	smallNumbersRuleBuilder2.word((index + 1).toString()).fromLeft().getRule() or
	smallNumbersRuleBuilder2.fromRight().getRule() or
	smallNumbersRuleBuilder3.word((index + 1).toString()).fromLeft().getRule() or
	smallNumbersRuleBuilder3.fromRight().getRule() or
	smallNumbersRuleBuilder4.map { it.word((index + 1).toString()).getRule() }
	.reduce { acc, cur -> acc or cur}
}

val RULE_DISALLOWED_WORDS = WordRuleBuilder()
		.called("слова \"theorem, definition, lemma\" не должны использоваться")
		.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY,PDFArea.TITLE_PAGE))
		.fromLeft()
		.ignoringPunctuation(true)
		.shouldNotHaveNeighbor(
				Regex("""[Tt]heorem"""),
				Regex("""[Dd]efinition"""),
				Regex("""[Ll]emma"""))
	.setDescription("Скорее всего было неправильно настроено окрудение. Подробнее написано тут: https://www.overleaf.com/learn/latex/Theorems_and_proofs и https://tex.stackexchange.com/questions/12913/customizing-theorem-name")
		.getRule()

val RULE_INCORRECT_ABBREVIATION = WordRuleBuilder()
		.called("Неправильное написание аббревиатуры \"вуз\"")
		.inArea(PDFRegion.EVERYWHERE)
		.ignoringPunctuation(true)
		.shouldNotHaveNeighbor(
				Regex("""ВУЗ(\p{Pd})?(.*)""") // detect "ВУЗ-", "ВУЗ", not "вуз","Вуз"
		)
	.setDescription("Неправильное написание аббревиатуры вуз. Оно встречается так часто, что мы сделали отдельное правило. Подробнее написано тут: https://ru.wiktionary.org/wiki/%D0%B2%D1%83%D0%B7")
		.getRule()

const val shortenedUrlRuleName = "Сокращённая ссылка"
val shortenedUrlRuleArea = PDFRegion.NOWHERE.except(PDFArea.FOOTNOTE, PDFArea.BIBLIOGRAPHY)

val urlShortenersListRule = URLRuleBuilder()
	.called(shortenedUrlRuleName)
	.setDescription("Нельзя пользоваться url-сокращателями. Ведь если сайт исчезнет или потеряет базу данных то работа потеряет в читабельности.")
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
	.setDescription("Нельзя пользоваться url-сокращателями. Ведь если сайт исчезнет или потеряет базу данных то работа потеряет в читабельности.")
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
	.setDescription("Web-ссылки должны быть максимально одинакового формата, не надо так смешивать: github.com и http://mail.ru.")
	.inArea(PDFRegion.NOWHERE.except(PDFArea.FOOTNOTE, PDFArea.BIBLIOGRAPHY))
	.disallow { urls ->
		var filteredUrls = urls.filter { url ->
			!url.text.startsWith("https://www") && !url.text.startsWith("http://www")
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
	.setDescription("Ссылки в квадратных скобках должны быть отсортированы (вот так плохо [2, 115, 4]) и подряд идущие должны быть с дефисами (вот так плохо [1, 2, 3, 4, 11], надо [1–4,11]). Для пишущих в TeX есть решение: https://stackoverflow.com/questions/3996844/how-do-i-cite-range-of-references-using-latex-and-bibtex")
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

val exclusionsForAbbreviations = arrayOf(Regex("или", RegexOption.IGNORE_CASE), Regex("по", RegexOption.IGNORE_CASE))

val RULE_VARIOUS_ABBREVIATIONS = RegexRuleBuilder()
	.called("Использованы различные версии сокращения")
	.setDescription("Тексте работ (не в ссылках!) часто пишут github, Github и GitHub. При этом, во-первых правильное написание — это GitHub, а во-вторых, в общем случае подобная разнородность смотрится некрасиво. Сейчас правило часто захватывает web-ссылки и потому возможны ложные срабатывания, однако следует проверить все ли из них — ложные (практика показывает что нет)")
	.regex(Regex("""[a-zA-Zа-яА-Я]+"""))
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.FOOTNOTE, PDFArea.BIBLIOGRAPHY))
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
		}.filter{ pair ->
			val word = pair.first
			exclusionsForAbbreviations.none { it.containsMatchIn(word) }
		}.map { it.second }
	}.getRule()

val RULE_LOW_QUALITY_CONFERENCES = URLRuleBuilder()
		.called("Ссылка на низкокачественную конференцию")
	.setDescription("Стоит проверить конференции на которые вы ссылаетесь")
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

const val fieldsCoordinateX = 555
val RULE_OUTSIDE_FIELDS = LineRuleBuilder()
	.called("Слово вышло за поля")
	.setDescription("При различных обстоятельствах слово может выйти за поля. Для пишущих в TeX есть решения: расставлять “\\-”, задавать \\hyphenation{слово}, выставить язык и другие")
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY, PDFArea.FOOTNOTE, PDFArea.TITLE_PAGE))
	.disallow { it ->
		it.filter {
			it.lastPosition.x > fieldsCoordinateX
		}
	}
	.getRule()

val RULE_DOUBLE_SPACE = SymbolRuleBuilder()
	.symbol(' ')
	.called("Два пробела подряд")
	.inArea(PDFRegion.EVERYWHERE)
	.shouldNotHaveNeighbor(' ')
	.getRule()

const val maxNumberOfBrackets = 5
val RULE_NUMBER_OF_BRACKETS = SentenceRuleBuilder()
	.called("Предложение с большим количеством скобок")
	.setDescription("В предложении не рекомендуется использовать больше $maxNumberOfBrackets пар скобок")
	.disallow { lines ->
		val results = mutableListOf<Line>()
		if(splitIntoSentences(lines, ".!?").any 
		{ sentence -> sentence.count{ it.text.contains('(') } > maxNumberOfBrackets})
		{ results.addAll(lines) }
		results.toList()
	}
	.getRule()

const val maxPercentageInBrackets = 25
val RULE_TEXT_IN_BRACKETS = SentenceRuleBuilder()
	.called("Большое количество текста внутри скобок")
	.setDescription("Стоит уменьшить количество текста внутри скобок. Рекомендуется помещать в скобки не более $maxPercentageInBrackets% от общего количества слов в предложении")
	.inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY))
	.disallow { lines ->
		val results = mutableListOf<Line>()
		if( splitIntoSentences(lines, ".!?").any { sentence -> 
			val sentenceText = sentence.joinToString(separator = " ") { it.text }
			val percentInBrackets = Regex("\\(([^)]+)\\)").findAll(sentenceText)
			.sumBy { words ->
				words.groupValues[1].split("\\s+".toRegex()).count()
			}.toDouble() / sentence.size * 100 
			percentInBrackets > maxPercentageInBrackets
		})
		{ results.addAll(lines) }
		results.toList()
	}
	.getRule()