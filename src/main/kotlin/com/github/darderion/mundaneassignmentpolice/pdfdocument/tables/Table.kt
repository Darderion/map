package com.github.darderion.mundaneassignmentpolice.pdfdocument.tables

import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Word
import org.jetbrains.kotlinx.dataframe.DataFrame

data class Table(val page : Int,
            val x1 : Double,
            val y1 : Double,
            val x2 : Double,
            val y2 : Double,
            val rowCount :  Int,
            val colCount : Int,
            val df: DataFrame<Any?>,
            var tableText: MutableList<Word>,
            var lineIndexes: MutableList<Int>
)