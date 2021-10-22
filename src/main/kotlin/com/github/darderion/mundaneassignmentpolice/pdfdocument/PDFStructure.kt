package com.github.darderion.mundaneassignmentpolice.pdfdocument

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section
import com.github.darderion.mundaneassignmentpolice.utils.floatEquals
import java.util.*

class PDFStructure(text: List<Text>) {
	val lists: List<PDFList<String>> = listOf()
	val sections: List<Section>
	val tableOfContents: PDFList<String>

	init {
		// Areas
		var area = TITLE_PAGE
		var sectionTitle: String? = null
		text.forEach {
			// For each line
			area = when(area) {
				TITLE_PAGE -> {
					if (it.content == TABLE_OF_CONTENT_TITLE) {
						TABLE_OF_CONTENT
					} else area
				}
				TABLE_OF_CONTENT -> {
					if (sectionTitle == null && it.content != TABLE_OF_CONTENT_TITLE && it.content.isNotEmpty()) {
						sectionTitle = it.text.first().text
						area
					} else {
						if (sectionTitle != null && it.line == 0 && it.content == sectionTitle) {
							SECTION
						} else area
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
					if (it.line == 0) {
						if (it.content == BIBLIOGRAPHY_TITLE) {
							BIBLIOGRAPHY
						} else {
							SECTION
						}
					} else area
				}
				else -> area
			}

			it.area = if (isPageIndex(text, it, area)) {
				PAGE_INDEX
			} else {
				area
			}
		}

		// Sections
		val sectionsTitles = text
			.filter { it.area == TABLE_OF_CONTENT }					// Only include lines from TABLE_OF_CONTENT
			.drop(1)												// Remove line with TABLE_OF_CONTENT_TITLE
			.map { it.content }										// filter STRING values
			.filter { it.isNotEmpty() }								//	remove empty lines
			.dropLast(1)											//	remove line with BIBLIOGRAPHY_TITLE
			.map { it.dropLast(2)								//	remove ' ' + NUMBER or NUMBER + NUMBER
				.dropLastWhile { it == ' ' || it == '.' }			// THIS FILTER ASSUMES THAT DOCUMENT CONTAINS
			}														//	LESS THAN 100 PAGES

		val sectionsTitlesWithIndexes = listOf("1. ${sectionsTitles[0]}") + sectionsTitles.drop(1).dropLast(1) + "${
			sectionsTitles.map { "${it[0]}${it[1]}".filter { it.isDigit() } }.filter { it.isNotEmpty() }.maxOf { it.toInt() } + 2
		}. ${sectionsTitles[sectionsTitles.count() - 1]}"
		
		val sectionText = text.filter { it.area == SECTION }

		sections = sectionsTitles.map { section ->					// Sections: List<Pair<String, Int>>
			section to sectionText									//	Section.first --> SectionTitle
				.dropLast(1)										//	Section.second --> SectionIndex
				.filterIndexed { index, text ->
					section == text.content || section == "${text.content} ${sectionText[index + 1].content}"
				}.first().documentIndex
		}.map { section -> Section(section.first, section.second,	// Sections: List<Section>
			sectionText.filter { it.documentIndex > section.second }[
					if (section.first == sectionText.first { it.documentIndex == section.second }.content) 0 else 1
				].documentIndex
		) }

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
	}

	companion object {
		private const val TABLE_OF_CONTENT_TITLE = "Оглавление"
		private const val BIBLIOGRAPHY_TITLE = "Список литературы"

		private const val FOOTNOTE_FONT_SIZE = 6.9738

		private fun isFootnote(line: Text) = line.content.isNotEmpty() &&
				line.text.first().text.isNotEmpty() &&
				floatEquals(line.text.first().font.size, FOOTNOTE_FONT_SIZE) &&
				line.text.first().text.first().isDigit() &&
				line.text.first().text.filterNot { it.isDigit() }.isEmpty()

		private fun isPageIndex(text: List<Text>, line: Text, area: PDFArea) = area != TITLE_PAGE &&
				line.line == (text.filter { textLine ->
			textLine.page == line.page && textLine.content.isNotEmpty()
		}.maxOfOrNull { it.line } ?: -1)
	}
}
