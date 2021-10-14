package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea

class Text(val line: Int, val page: Int, val documentIndex: Int,
		   private val text: List<Word>,
		   val area: PDFArea
) {
	val content: String
	get() = text.joinToString("") { it.text }

	override fun toString() = "[$documentIndex -- $line, p.$page, $area] --> '$content'"
}
