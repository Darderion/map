package com.github.darderion.mundaneassignmentpolice.pdfdocument.tables

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.*
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox
import org.jetbrains.kotlinx.dataframe.AnyFrame
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.*

class Table(val df: DataFrame<Any?>){

    val page : Int
    val x1 : Double
    val y1 : Double
    val x2 : Double
    val y2 : Double
    val rowCount :  Int
    val colCount : Int
    val cells: MutableList<Cell> = mutableListOf()
    init {
        val indexTableInf = df.select{ cols(0) }.last { it[0] == "table information"}.index()
        val tableInf = df.select{cols(0)}.filter { it.index() >= indexTableInf }

        this.page = tableInf[pageTableIndex][0].toString().toInt() - 1
        this.x1 = tableInf[x1TableIndex][0].toString().toDouble()
        this.y1 = defaultPageHeight - tableInf[y1TableIndex][0].toString().toDouble()
        this.x2 = tableInf[x2TableIndex][0].toString().toDouble()
        this.y2 = defaultPageHeight - tableInf[y2TableIndex][0].toString().toDouble()
        this.rowCount = tableInf[rowTableIndex][0].toString().toInt()
        this.colCount = tableInf[colTableIndex][0].toString().toInt()
        val tableData  = df.filter { it.index() < indexTableInf }

        tableData.forEachColumn { it.forEach { getCell(it.toString()) } }
    }

    private fun getCell(text: String){

        val coordinates = text.lines().first().split(" ")
        val x1 = coordinates[x1CellIndex].toDouble()
        val y1 = defaultPageHeight - coordinates[y1CellIndex].toDouble()
        val x2 = coordinates[x2CellIndex].toDouble()
        val y2 = defaultPageHeight - coordinates[y2CellIndex].toDouble()

        val cellText = text.lines().filterIndexed{ index, _ -> index > 0 }.toMutableList()

        cells.add(Cell(page, cellText, mutableListOf(), Coordinate(x1,y1), Coordinate(x2,y2)))
    }

    companion object {
        private const val defaultPageHeight = 842.0
        private const val x1CellIndex = 2
        private const val y1CellIndex = 5
        private const val x2CellIndex = 8
        private const val y2CellIndex = 11

        private const val pageTableIndex = 2
        private const val x1TableIndex = 4
        private const val y1TableIndex = 5
        private const val x2TableIndex = 6
        private const val y2TableIndex = 7
        private const val rowTableIndex = 9
        private const val colTableIndex = 11
    }
}
