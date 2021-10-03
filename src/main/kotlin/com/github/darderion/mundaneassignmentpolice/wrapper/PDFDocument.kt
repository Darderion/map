package com.github.darderion.mundaneassignmentpolice.wrapper

class PDFDocument(val name: String, val text: List<PDFText>) {
	override fun toString() = "PDF: $name\n" +
			text.joinToString("\n") { it.toString() }

	fun toHTMLString() = text.joinToString("<br>") { it.content }
}
