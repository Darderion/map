package com.github.darderion.mundaneassignmentpolice.pdfdocument

class PDFText(val line: Int, val page: Int, val documentIndex: Int, val content: String) {
	override fun toString() = "[$documentIndex -- $line, p.$page] --> '$content'"
}
