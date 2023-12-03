package com.github.darderion.mundaneassignmentpolice.pdfdocument

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.pdfdocument.list.PDFList
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section
import com.github.darderion.mundaneassignmentpolice.utils.CodeDetector
import com.github.darderion.mundaneassignmentpolice.utils.floatEquals
import java.util.*

class PDFStructure(text: List<Line>) {
    val lists: List<PDFList<Line>>
    val sections: List<Section>
    val tableOfContents: PDFList<String>

    private fun String.clearSymbols() = this.replace("-", "").replace(" ", "").replace(".", "")
    init {
        // Areas
        var area = TITLE_PAGE
        var sectionTitle: String? = null
        var isAfterBibliography = false
        var sectionAfterBibliographyTitle: String? = null
        text.forEach {
            // For each line
            area = when (area) {
                TITLE_PAGE -> {
                    if (TABLE_OF_CONTENT_TITLES.contains(it.content.trim())) {
                        TABLE_OF_CONTENT
                    } else area
                }

                TABLE_OF_CONTENT -> {
                    if (sectionTitle == null && !TABLE_OF_CONTENT_TITLES.contains(it.content.trim()) && it.content.isNotEmpty()) {
                        // sectionTitle = it.text.first().text
                        sectionTitle = it.text.filter { it.text.trim().isNotEmpty() }.dropLast(1).joinToString(" ")
                        area
                    } else {
                        if (sectionTitle != null && it.index == 0 && it.content == sectionTitle) {
                            SECTION
                        } else {
                            if (isAfterBibliography && sectionAfterBibliographyTitle == null) {
                                sectionAfterBibliographyTitle =
                                    it.text.filter { it.text.trim().isNotEmpty() }.dropLast(1).joinToString(" ")
                                        .ifBlank { null }
                                isAfterBibliography = false
                            } else if (removeNumberingAndPage(it.content) == BIBLIOGRAPHY_TITLE) {
                                isAfterBibliography = true
                            }
                            area
                        }
                    }
                }

                SECTION -> {
                    if (isFootnote(it)) {
                        FOOTNOTE
                    } else {
                        if (it.content == BIBLIOGRAPHY_TITLE) {
                            BIBLIOGRAPHY
                        } else area
                    }
                }

                FOOTNOTE -> {
                    if (it.index == 0) {
                        if (it.content == BIBLIOGRAPHY_TITLE) {
                            BIBLIOGRAPHY
                        } else {
                            SECTION
                        }
                    } else area
                }

                BIBLIOGRAPHY -> {
                    if (sectionAfterBibliographyTitle != null && it.index == 0 && it.content == sectionAfterBibliographyTitle) {
                        SECTION
                    } else area
                }
                else -> area
            }
            it.area = if (isPageIndex(text, it, area)) {
                PAGE_INDEX
            } else if (CodeDetector.isLikelyCode(it.content)) CODE
            else {
                area
            }
        }

        val sectionsWithoutIndexes = listOf<String>(
            "Введение", "Заключение", "Приложение"
        )

        // Sections
        val tableOfContentsText = text.filter { it.area == TABLE_OF_CONTENT }

        var curSectionTitle = ""
        val sectionsTitlesLines = mutableListOf<String>()

        tableOfContentsText                                            // Only include lines from TABLE_OF_CONTENT
            .drop(1)                                                // Remove line with TABLE_OF_CONTENT_TITLE
            .map { it.content }                                        // filter STRING values
            .filter { it.isNotEmpty() }                                //	remove empty lines
            .filterNot {                                        //	remove line with BIBLIOGRAPHY_TITLE
                removeNumberingAndPage(it) == BIBLIOGRAPHY_TITLE
            }.forEach {
                if (it[it.length - 1].isDigit() || it[it.length - 2].isDigit()) {
                    sectionsTitlesLines.add(curSectionTitle + it)
                    curSectionTitle = ""
                } else {
                    curSectionTitle += "$it "
                }
            }

        val sectionsTitles = sectionsTitlesLines.map {
            it.dropLastWhile { it.isDigit() }.dropLastWhile { it == ' ' || it == '.' }
        }

        val sectionsTitlesWithIndexes = sectionsTitles.map { if (it.contains('.')) it else ". $it" }

        val sectionTextLines = text.filter { it.area == SECTION }.map {
                it.documentIndex to it.content.clearSymbols()
            }.dropLast(1)

        val sectionsIndexed = mutableListOf<Section>()

        val titlesIterator = sectionsTitles.iterator()

        var lineIndex = 0
        while (titlesIterator.hasNext()) {
            val title = titlesIterator.next()
            var numberOfLines = 1
            var titleLines = sectionTextLines[lineIndex].second

            while (title.clearSymbols() != titleLines) {
                if (titleLines.isNotEmpty() && title.clearSymbols().startsWith(titleLines)) {
                    titleLines += sectionTextLines[lineIndex + numberOfLines].second
                    numberOfLines++
                } else {
                    numberOfLines = 1
                    lineIndex++
                    titleLines = sectionTextLines[lineIndex].second
                }
            }

            val titleIndex = sectionTextLines[lineIndex].first
            val contentIndex = if (text[titleIndex + numberOfLines].area == SECTION) titleIndex + numberOfLines else -1
            sectionsIndexed.add(
                Section(title, titleIndex, contentIndex)
            )
            lineIndex += numberOfLines
        }

        sections = sectionsIndexed

        tableOfContents = PDFList("TABLE_OF_CONTENTS")

        val stack: Stack<PDFList<String>> = Stack()

        stack.push(tableOfContents)

        sectionsTitlesWithIndexes.forEach { section ->
            val listIndentation = section.count { it == '.' }
            val item = section.split('.').last().drop(1)

            if (listIndentation > stack.count()) {
                stack.push(stack.peek().nodes.last())
            } else {
                if (listIndentation < stack.count()) {
                    repeat(stack.count() - listIndentation) {
                        stack.pop()
                    }
                }
            }

            stack.peek().nodes.add(PDFList(item))
        }

        lists = PDFList.getLists(text.filter { line ->
            line.area == SECTION && !isSectionTitle(line) && line.content.isNotEmpty()
        })

        if (text.none { it.area == SECTION }) throw Error("No content section in PDF")
        if (text.none { it.area == TABLE_OF_CONTENT }) throw Error("No TABLE_OF_CONTENT in PDF")
        if (text.none { it.area == BIBLIOGRAPHY }) throw Error("No BIBLIOGRAPHY in PDF")
    }

    private fun isSectionTitle(line: Line) = sections.any {
        (if (it.contentIndex == it.titleIndex + 2) line.documentIndex == it.titleIndex + 1
        else false) || line.documentIndex == it.titleIndex
    }

    companion object {

        private val TABLE_OF_CONTENT_TITLES = listOf("Оглавление", "Содержание")
        private const val BIBLIOGRAPHY_TITLE = "Список литературы"

        private const val FOOTNOTE_FONT_SIZE = 6.9738

        private fun isFootnote(line: Line) =
            line.content.isNotEmpty() && line.text.first().text.isNotEmpty() && floatEquals(
                line.text.first().font.size,
                FOOTNOTE_FONT_SIZE
            ) && line.text.first().text.first().isDigit() && line.text.first().text.filterNot { it.isDigit() }.isEmpty()

        private fun isPageIndex(text: List<Line>, line: Line, area: PDFArea) =
            area != TITLE_PAGE && line.index == (text.filter { textLine ->
                textLine.page == line.page && textLine.content.isNotEmpty()
            }.maxOfOrNull { it.index } ?: -1)

        private fun removeNumberingAndPage(tableOfContentLine: String) =
            tableOfContentLine.dropWhile { it.isDigit() || it == '.' }.dropLastWhile { it.isDigit() }.trim()
    }
}
