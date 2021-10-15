package com.github.darderion.mundaneassignmentpolice.pdfdocument

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text

class PDFDocument(val name: String, val text: List<Text>) {
	override fun toString() = "PDF: $name\n" +
			text.joinToString("\n") { it.toString() }

	val areas: PDFStructure = PDFStructure(text)

	fun toHTMLString() = text.joinToString("<br>") { it.content }

	fun toTextList() = text.map { it.content }

	fun getTextFromLines(fromIndex: Int, toIndex: Int) = text.filterIndexed { index, pdfText ->
		index in fromIndex..toIndex
	}.joinToString("\n ") { it.content }

	fun print() {
		text.map { "${it.area} | ${it.content}" }.forEach(::println)
	}

	/*
	fun getLists(): List<PDFDocument> {
		/*
		Lines that start with a number are either subsections' titles, page counters or parts of title page
		First line with '1.' should be a line from table of contents
		 */
		val lines = text.filter {
			it.content.length > 3 &&									// Lists are either:
					((it.content.first() == '•') ||						//		Itemize
					(it.content.first().isDigit() &&					//		Enumerate
					((it.content[1].isDigit() && it.content[2] == '.')	//			No lists with 100+ elements
							|| it.content[1] == '.')))					//			No multi-level lists
		}
	}
	 */
}
