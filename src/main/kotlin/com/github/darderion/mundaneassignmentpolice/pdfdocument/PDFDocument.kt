package com.github.darderion.mundaneassignmentpolice.pdfdocument

class PDFDocument(val name: String, val text: List<PDFText>) {
	override fun toString() = "PDF: $name\n" +
			text.joinToString("\n") { it.toString() }

	fun toHTMLString() = text.joinToString("<br>") { it.content }

	fun toTextList() = text.map { it.content }

	fun getTextFromLines(fromIndex: Int, toIndex: Int) = text.filterIndexed { index, pdfText ->
		index in fromIndex..toIndex
	}.joinToString("\n ") { it.content }

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
