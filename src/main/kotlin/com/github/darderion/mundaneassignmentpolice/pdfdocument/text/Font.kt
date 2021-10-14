package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

import kotlin.math.abs

fun floatEquals(a: Float, b: Float) = abs(a - b) < 0.0000001

class Font(val size: Float) {
	override fun equals(other: Any?) = this === other ||
			(other is Font && floatEquals(size, other.size))

	override fun hashCode() = size.hashCode()
}
