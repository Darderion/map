package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

data class Symbol(
	private val text: String,
	val font: Font
) {
	override fun toString() = text
}
