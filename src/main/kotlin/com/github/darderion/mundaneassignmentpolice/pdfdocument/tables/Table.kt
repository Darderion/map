package com.github.darderion.mundaneassignmentpolice.pdfdocument.tables

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Coordinate
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.forEachColumn

data class Table(val page : Int,
                 val x1 : Double,
                 val y1 : Double,
                 val x2 : Double,
                 val y2 : Double,
                 val rowCount :  Int,
                 val colCount : Int,
                 val df: DataFrame<Any?>,
                 var cells: MutableList<Cell>
){
    init {
        df.forEachColumn { it.forEach { getCell(it.toString()) } }
    }

    private fun getCell(text: String){

        val coordinates = text.lines().first().split(" ")

        val x1 = coordinates[x1CellIndex].toDouble()
        val y1 = defaultPageHeight - coordinates[y1CellIndex].toDouble()
        val x2 = coordinates[x2CellIndex].toDouble()
        val y2 = defaultPageHeight - coordinates[y2CellIndex].toDouble()

        val cellText = text.lines().filterIndexed { index, _ -> index != 0 }

        cells.add(Cell(page, cellText, Coordinate(x1,y1), Coordinate(x2,y2)))
    }

    companion object {
        private const val defaultPageHeight = 842.0
        private const val x1CellIndex = 2
        private const val y1CellIndex = 5
        private const val x2CellIndex = 8
        private const val y2CellIndex = 11
    }
}
