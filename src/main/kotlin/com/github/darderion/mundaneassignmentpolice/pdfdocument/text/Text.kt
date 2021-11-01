package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea

data class Text(val line: Int, val page: Int, val documentIndex: Int,
		   val text: List<Word>, var area: PDFArea? = null
) {
	val content: String
	get() = text.joinToString("") { it.text }

	val position: Coordinate
	get() = if (text.isNotEmpty()) text.first().position else Coordinate(0, 0)

	val first: String?
	get() = if (text.isNotEmpty()) text.first().text else null

	val second: String?
	get() = if (text.count() > 1) text[1].text else null

	override fun toString() = "[$documentIndex -- $line, p.$page, $area, ${position.x}] --> '$content'"

	fun drop(numberOfItems: Int) = Text(line, page, documentIndex, text.drop(numberOfItems), area)
}
