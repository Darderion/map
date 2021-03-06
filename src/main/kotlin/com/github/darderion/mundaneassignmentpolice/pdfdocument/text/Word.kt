package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

data class Word(val text: String, val font: Font, val position: Coordinate) {
	override fun toString() = text

	companion object {
		val spaceCharacter: Word
		get() = Word(" ", Font(0.0f), Coordinate(0, 0))
	}
}
