package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

data class Symbol(
	private val text: Char,
	val font: Font,
	val position: Coordinate
) {
	override fun toString() = text.toString()
}
