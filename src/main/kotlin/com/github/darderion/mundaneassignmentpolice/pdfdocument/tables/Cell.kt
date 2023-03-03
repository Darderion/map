package com.github.darderion.mundaneassignmentpolice.pdfdocument.tables
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate

class Cell(
    val page: Int,
    val cellText: List<String>,
    val leftCorner: Coordinate,
    val rightCorner: Coordinate
)