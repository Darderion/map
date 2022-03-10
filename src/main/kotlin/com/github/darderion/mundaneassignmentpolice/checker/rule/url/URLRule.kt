package com.github.darderion.mundaneassignmentpolice.checker.rule.url

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.inside
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class URLRule(
    val predicates: List<(urls: List<Pair<String, Line>>) -> List<Line>>,
    type: RuleViolationType,
    name: String,
): Rule(PDFRegion.NOWHERE.except(PDFArea.FOOTNOTE, PDFArea.BIBLIOGRAPHY), name, type) {
    override fun process(document: PDFDocument): List<RuleViolation> {
        val ruleViolations: MutableList<RuleViolation> = mutableListOf()

        val urls = getAllUrls(document, area)
        predicates.forEach { predicate ->
            predicate(urls).map { RuleViolation(listOf(it), name, type) }.forEach {
                ruleViolations.add(it)
            }
        }
        return ruleViolations
    }

    private fun getAllUrls(document: PDFDocument, area: PDFRegion): List<Pair<String, Line>> {
        val urls: MutableList<Pair<String, Line>> = mutableListOf()
        val urlRegex = Regex("""^((https?://)|(www\.))[^\s]+""")

        val linesInsideArea = document.text.filter { it.area!! inside area }
        var lineIndex = 0
        while (lineIndex < linesInsideArea.size) {
            val line = linesInsideArea[lineIndex]
            line.text.map { it.text }.forEachIndexed forEachWord@{ wordIndex, word ->
                if (!urlRegex.matches(word)) return@forEachWord

                if (wordIndex != line.text.lastIndex) {
                    urls.add(word to line)
                    return@forEachWord
                }

                var multilineUrl = word
                var currentWord = word
                var nextLine = linesInsideArea[lineIndex + 1]
                while (":/._".contains(currentWord.last()) && nextLine.text.isNotEmpty()) {
                    currentWord = nextLine.text.first().text
                    multilineUrl += currentWord
                    if (nextLine.text.size != 1) break
                    lineIndex++
                    nextLine = linesInsideArea[lineIndex + 1]
                }
                urls.add(multilineUrl to line)
            }
            lineIndex++
        }
        return urls
    }
}