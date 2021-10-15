package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

import com.github.darderion.mundaneassignmentpolice.utils.floatEquals

class Font(val size: Float) {
	override fun equals(other: Any?) = this === other ||
			(other is Font && floatEquals(size, other.size))

	override fun hashCode() = size.hashCode()
}
