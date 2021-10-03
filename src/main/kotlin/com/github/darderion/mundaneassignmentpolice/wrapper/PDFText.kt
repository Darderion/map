package com.github.darderion.mundaneassignmentpolice.wrapper

class PDFText(val line: Int, val page: Int, val content: String) {
	override fun toString() = "[$line, p.$page] --> '$content'"
}
