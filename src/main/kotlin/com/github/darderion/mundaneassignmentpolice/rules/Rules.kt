package com.github.darderion.mundaneassignmentpolice.rules

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.Section
import com.github.darderion.mundaneassignmentpolice.checker.getPages
import com.github.darderion.mundaneassignmentpolice.checker.rule.list.ListRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.regex.RegexRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.line.LineRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.SymbolRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.and
import com.github.darderion.mundaneassignmentpolice.checker.rule.symbol.or
import com.github.darderion.mundaneassignmentpolice.checker.rule.tableofcontent.TableOfContentRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.url.URLRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.WordRule
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.WordRuleBuilder
import com.github.darderion.mundaneassignmentpolice.checker.rule.word.or
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.utils.InvalidOperationException
import com.github.darderion.mundaneassignmentpolice.utils.LowQualityConferencesUtil
import com.github.darderion.mundaneassignmentpolice.utils.URLUtil
import java.io.File
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
    .inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY, PDFArea.FOOTNOTE))

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

val RULE_BRACKETS_LETTERS = SymbolRuleBuilder()
    .symbol(bracket)
    .ignoringAdjusting(' ')
    .fromRight().shouldNotHaveNeighbor(*rusCapitalLetters.toCharArray())
    .called("Большая русская буква после скобки")
    .type(RuleViolationType.Warning)
    .getRule()

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

val RULE_SINGLE_SUBSECTION = ListRuleBuilder()
    .inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
    //.called("Only 1 subsection in a section")
    .called("Одна подсекция в секции")
    .disallowInSingleList {
        if (it.nodes.count() == 1) it.nodes.first().getText() else listOf()
    }.getRule()

val RULE_TASKS_MAPPING = ListRuleBuilder()
    .inArea(PDFArea.SECTION)
    .called("Задачи и результаты не совпадают")
    .addListsFilter { _, document ->
        val newLists = mutableListOf<PDFList<Line>>()
        val tasks = mutableListOf<PDFList<Line>>()
        val results = mutableListOf<PDFList<Line>>()
        val taskPages = getPages(document, "адач") // Задачи, задачи, задач, задача
        val conclusionPages = getPages(document, "Заключение")
        if (taskPages != -1 to -1 && conclusionPages != -1 to -1) {
            document.areas!!.lists.forEach {
                if (it.getText()[0].page >= taskPages.first && it.getText()[0].page < taskPages.second)
                    tasks.add(it)
                if (it.getText()[0].page >= conclusionPages.first)
                    results.add(it)
            }
        }
        newLists.addAll(tasks)
        newLists.addAll(results)
        newLists
    }
    .disallowInMultipleListsWithDocument { lists, document ->
        val taskPages = getPages(document,"адач")
        val conclusionPages = getPages(document, "Заключение")
        var tasks = mutableListOf<PDFList<Line>>()
        var results = mutableListOf<PDFList<Line>>()

        lists.forEach {
            if (it.getText()[0].page >= taskPages.first && it.getText()[0].page < taskPages.second)
                tasks.add(it)
            if (it.getText()[0].page >= conclusionPages.first)
                results.add(it)
        }
        if (taskPages == -1 to -1 || conclusionPages == -1 to -1) listOf()
        else {
            val tasksAndResultsSections = document.areas!!.sections
                .filter { it.title.contains("адач") || it.title.contains("Заключение") }
            if (tasks.isEmpty() && results.isEmpty()) {
                listOf( // underline "задачи" "Заключение"
                    document.text[tasksAndResultsSections.first().titleIndex],
                    document.text[tasksAndResultsSections.last().titleIndex]
                )
            } else if (tasks.isEmpty() && results.isNotEmpty())
                listOf(document.text[tasksAndResultsSections.first().titleIndex]) //underline "задачи"
            else if (tasks.isNotEmpty() && results.isEmpty())
                listOf(document.text[tasksAndResultsSections.last().titleIndex])//underline "Заключение"
            else if (tasks.isNotEmpty() && results.isNotEmpty()) {
                if (results.size != results.filter { it.nodes.size < tasks[0].nodes.size }.toMutableList().size)
                    listOf()
                else {
                    results = results.filter { it.nodes.size < tasks[0].nodes.size }.toMutableList()
                    results[0].getText()//all lists in conclusion are less than task lists
                    //underline first list in conclusion
                }
            } else listOf()
        }
    }
    .getRule()

val RULE_NO_TASKS = TableOfContentRuleBuilder()
    .called("Задачи не выделены в содержании")
    .disallow {
        val tasks = it.filter { it.text.toString().contains("адач") }
        if (tasks.isEmpty()) listOf(it.first()) else listOf()
    }
    .getRule()

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
                    .filterNot { it.text.contains("[0-9]+\\.".toRegex()) }        // remove numbering
                words.isEmpty() || words[0].text == Section.TABLE_OF_CONTENT.title
            }
            .filter { line ->
                val words = line.text
                    .filter { it.text.trim().isNotEmpty() }
                    .filterNot { it.text.contains("[0-9]+\\.".toRegex()) }        // remove numbering

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

val smallNumbersRuleBuilder1 = WordRuleBuilder()        //for nearest words
    .called(smallNumbersRuleName)
    .inArea(smallNumbersRuleArea)
    .ignoringAdjusting(Regex("""\s"""), Regex("""\."""))
    .ignoringIfIndex(0)

val smallNumbersRuleBuilder2 = WordRuleBuilder()        //for decimal fractions and version numbers
    .called(smallNumbersRuleName)
    .inArea(smallNumbersRuleArea)
    .shouldHaveNeighbor(
        Regex("""\."""), Regex(""","""),
        Regex("""[0-9]+""")
    )
    .shouldHaveNumberOfNeighbors(2)

val smallNumbersRuleBuilder3 = WordRuleBuilder()        //for links
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

val RULE_SHORTENED_URLS = URLRuleBuilder()
    .called("Сокращённая ссылка")
    .inArea(PDFRegion.NOWHERE.except(PDFArea.FOOTNOTE, PDFArea.BIBLIOGRAPHY))
    .disallow { urls ->
        urls.filter { pair ->
            try {
                var url = pair.first
                if (!url.startsWith("http")) url = "http://$url"
                URLUtil.isShortened(url)
            } catch (_: InvalidOperationException) {
                false
            }
        }.map { it.second }
    }.getRule()

val RULE_URLS_UNIFORMITY = URLRuleBuilder()
    .called("Ссылки разных видов")
    .disallow { urls ->
        var filteredUrls = urls.filter { pair ->
            val url = pair.first
            !url.startsWith("https://www")
        }
        if (urls.size == filteredUrls.size) {
            filteredUrls = filteredUrls.filter { pair ->
                val url = pair.first
                !url.startsWith("www")
            }
            if (urls.size == filteredUrls.size) {
                filteredUrls = filteredUrls.filter { pair ->
                    val url = pair.first
                    !url.startsWith("htt")
                }
            }
        }
        filteredUrls.map { it.second }
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
                    .count { it.isUpperCase() } > 0
            )
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
        urls.filter { pair ->
            val url = pair.first
            lowQualityConferencesList
                .any { conference -> url.contains(conference) }
        }.map { it.second }
    }.getRule()

const val precisionWordCount = 5
const val fileConfigurationWordsName= "src/main/resources/HardSoftConfigurationWords.txt"
val RULE_CONFIGURATION_IN_EXPERIMENTS =  LineRuleBuilder()
        .addLinesFilter { _, document ->
                var experimentsPages = getPages(document, "ксперимент").first to //Эксперимент, экспериментов, эксперимент
                        getPages(document, "Заключение").first
                if (experimentsPages.first == -1)
                    experimentsPages = getPages(document, "Тестирование").first to getPages(document, "Заключение").first

                if (experimentsPages.first != -1) {
                    document.text.filter { line ->
                        line.text.isNotEmpty() && line.page >= experimentsPages.first && line.page < experimentsPages.second
                    }
                } else listOf()
            }
        .disallowInMultipleLines { lines, _ ->
            var wordCount = 0
            val hardConfigurationWords: MutableList<String> = mutableListOf()
            File(fileConfigurationWordsName).forEachLine { hardConfigurationWords.add(it) }

            lines.forEach { line -> line.text.forEach {
                    val word = it.text
                    hardConfigurationWords.forEach { if (word.contains(it)) wordCount += 1 }
                }
            }

            if (wordCount < precisionWordCount)
                listOf(lines.first())
            else listOf()
        }
        .called("Нет hard/soft конфигурации в экспериментах")
        .getRule()
