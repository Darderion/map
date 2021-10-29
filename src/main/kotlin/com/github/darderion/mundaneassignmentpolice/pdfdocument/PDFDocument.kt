package com.github.darderion.mundaneassignmentpolice.pdfdocument

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Text
import java.lang.Exception

class PDFDocument(val name: String, val text: List<Text>) {
	override fun toString() = "PDF: $name\n" +
			text.joinToString("\n") { it.toString() }

	val areas: PDFStructure? = try {
		PDFStructure(text)
	} catch (e: Exception) {
		println(e.message)
		null
	} catch (e: Error) {
		println(e.message)
		null
	}

	fun toHTMLString() = text.joinToString("<br>") { it.content }

	fun toTextList() = text.map { it.content }

	fun getTextFromLines(fromIndex: Int, toIndex: Int, region: PDFRegion) = text.filter { it.area!! inside region }
		.filterIndexed { index, pdfText ->
		index in fromIndex..toIndex
	}.joinToString("\n ") { it.content }

	fun print() {
		text.map { "${it.area} | ${it.text.joinToString("--") { "${it.font.size}-${it.text}"}}" }.forEach(::println)
	}
}
