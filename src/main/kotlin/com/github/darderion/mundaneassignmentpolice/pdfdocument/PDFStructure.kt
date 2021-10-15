package com.github.darderion.mundaneassignmentpolice.pdfdocument

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea.*
import com.github.darderion.mundaneassignmentpolice.utils.floatEquals

class PDFStructure(val text: List<Text>) {
	init {
		var area = TITLE_PAGE
		var sectionTitle: String? = null
		text.forEach {
			when(area) {
				TITLE_PAGE -> {
					if (it.content == TABLE_OF_CONTENT_TITLE) {
						area = TABLE_OF_CONTENT
					}
				}
				TABLE_OF_CONTENT -> {
					if (sectionTitle == null && it.content != TABLE_OF_CONTENT_TITLE && it.content.isNotEmpty()) {
						sectionTitle = it.text.first().text
					} else {
						if (sectionTitle != null && it.line == 0 && it.content == sectionTitle) {
							area = SECTION
						}
					}
				}
				SECTION -> {
					if (floatEquals(it.text.first().font.size, FOOTNOTE_FONT_SIZE)) {

					}
					if (isFootnote(it)) {
						area = FOOTNOTE
					} else {
						if (it.content == BIBLIOGRAPHY_TITLE) {
							area = BIBLIOGRAPHY
						}
					}
				}
				FOOTNOTE -> {
					if (it.line == 0) {
						area = if (it.content == BIBLIOGRAPHY_TITLE) {
							BIBLIOGRAPHY
						} else {
							SECTION
						}
					}
				}
			}

			it.area = if (area != TITLE_PAGE &&
				it.line == (text.filter { line ->
					line.page == it.page && line.content.isNotEmpty()
				}.maxOfOrNull { it.line } ?: -1)
			) {
				PAGE_INDEX
			} else {
				area
			}
		}
	}

	companion object {
		const val TABLE_OF_CONTENT_TITLE = "Оглавление"
		const val BIBLIOGRAPHY_TITLE = "Список литературы"

		private const val FOOTNOTE_FONT_SIZE = 6.9738

		fun isFootnote(line: Text) = line.content.isNotEmpty() &&
				line.text.first().text.isNotEmpty() &&
				floatEquals(line.text.first().font.size, FOOTNOTE_FONT_SIZE) &&
				line.text.first().text.first().isDigit() &&
				line.text.first().text.filterNot { it.isDigit() }.isEmpty()
	}
}
