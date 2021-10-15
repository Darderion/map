package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

data class Symbol(
	private val text: String,
	val font: Font,
	val position: Pair<Float, Float>
) {
	override fun toString() = text
}
