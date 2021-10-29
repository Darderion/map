package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

import com.github.darderion.mundaneassignmentpolice.utils.floatEquals
import com.github.darderion.mundaneassignmentpolice.utils.nearby

class Coordinate(x: Number, y: Number) {
	val x: Float = x.toFloat()
	val y: Float = y.toFloat()

	infix fun hasSameXAs(position: Coordinate) = this.x nearby position.x
		// floatEquals(this.x, position.x)

	constructor(coordinates: Pair<Number, Number>): this(coordinates.first, coordinates.second)
}