package com.github.darderion.mundaneassignmentpolice.pdfdocument.text

class Coordinate(x: Number, y: Number) {
	val x: Float = x.toFloat()
	val y: Float = y.toFloat()

	constructor(coordinates: Pair<Number, Number>): this(coordinates.first, coordinates.second)
}