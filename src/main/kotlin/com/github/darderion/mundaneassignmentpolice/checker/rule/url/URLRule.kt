package com.github.darderion.mundaneassignmentpolice.checker.rule.url

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.utils.nearby

class URLRule(
    val predicates: List<(urls: List<Pair<String, List<Line>>>) -> List<List<Line>>>,
    type: RuleViolationType,
    area: PDFRegion,
    name: String
): Rule(area, name, type) {
    override fun process(document: PDFDocument): List<RuleViolation> {
        val ruleViolations: MutableList<RuleViolation> = mutableListOf()

        val urls = getAllUrls(document)
        predicates.forEach { predicate ->
            predicate(urls).map { RuleViolation(it, name, type) }.forEach {
                ruleViolations.add(it)
            }
        }
        return ruleViolations
    }

    private fun getAllUrls(document: PDFDocument): List<Pair<String, List<Line>>> {
        val urls: MutableList<Pair<String, List<Line>>> = mutableListOf()
        val urlRegex = Regex("""^((https?:)|(www\.))[^\s]*""")

        val bibliographyIndent = document.text.filter {
            it.area == PDFArea.BIBLIOGRAPHY
        }.getOrNull(1)?.text?.getOrNull(2)?.position?.x ?: -1.0f


        val linesInsideArea = document.text.filter { it.area!! inside area }
        var lineIndex = 0
        while (lineIndex < linesInsideArea.size) {
            val line = linesInsideArea[lineIndex]
            line.text.map { it.text }.forEachIndexed forEachWord@{ wordIndex, word ->
                if (!urlRegex.matches(word)) return@forEachWord

                if (wordIndex != line.text.lastIndex || lineIndex == linesInsideArea.lastIndex) {
                    urls.add(word to listOf(line))
                    return@forEachWord
                }

                var multilineUrl = word
                val urlLines = mutableListOf(line)
                var currentWord = word
                val currentArea = line.area
                val currentFontSize = line.text.last().font.size

                var nextLine = linesInsideArea[lineIndex + 1]
                var nextWord = nextLine.first ?: ""

                while (currentWord.last() in ":/._-" && nextWord.isNotEmpty() && nextLine.area == currentArea) {
                    if (currentArea == PDFArea.FOOTNOTE && !nextLine.text.first().font.size.nearby(currentFontSize))
                        break
                    if (currentArea == PDFArea.BIBLIOGRAPHY && !nextLine.position.x.nearby(bibliographyIndent))
                        break

                    currentWord = nextWord
                    multilineUrl += currentWord
                    urlLines.add(nextLine)

                    if (nextLine.text.size > 1 || lineIndex == linesInsideArea.lastIndex)
                        break

                    lineIndex++
                    nextLine = linesInsideArea[lineIndex + 1]
                    nextWord = nextLine.first ?: ""
                }
                multilineUrl = multilineUrl.dropLastWhile { it == '.' }
                urls.add(multilineUrl to urlLines)
            }
            lineIndex++
        }
        return urls
    }
}
