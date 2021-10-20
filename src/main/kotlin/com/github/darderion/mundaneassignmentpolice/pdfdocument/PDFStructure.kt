package com.github.darderion.mundaneassignmentpolice.pdfdocument

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.utils.floatEquals

class PDFStructure(val text: List<Text>) {
	init {
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
