package com.github.darderion.mundaneassignmentpolice.pdfdocument.tables
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

data class Cell(
    val page: Int,
    val cellText: MutableList<String>,
    var cellLines: MutableList<Line>,
    val leftCorner: Coordinate,
    val rightCorner: Coordinate
)