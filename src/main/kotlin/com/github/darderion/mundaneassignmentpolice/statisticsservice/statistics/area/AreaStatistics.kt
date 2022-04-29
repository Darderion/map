package com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics.area

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea

abstract class AreaStatistics(val area: PDFArea, val areaSizeInPage: Int, val documentSize: Int) {
    val areaSizeInPercents = areaSizeInPage.toFloat() / documentSize * 100
}
