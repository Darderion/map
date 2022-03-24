package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

import com.github.darderion.mundaneassignmentpolice.utils.floatEquals

enum class PostScriptFontType {
	TYPE0, TYPE1, TYPE2, TYPE3, NONE
}

class Font(val type: PostScriptFontType, val size: Float) {
	constructor(): this(PostScriptFontType.NONE, 0.0f)

	override fun equals(other: Any?) = this === other ||
			(other is Font && type == other.type && floatEquals(size, other.size))

	override fun hashCode() = (type to size).hashCode()
}
