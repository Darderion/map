package com.github.darderion.mundaneassignmentpolice.statisticsservice

import com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics.GeneralStatistic
import com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics.StatisticsBuilder
import com.github.darderion.mundaneassignmentpolice.wrapper.PDFBox

class StatisticsService {
    fun getPDFStatistic(pdfName: String) : GeneralStatistic{
        val document = PDFBox().getPDF(pdfName)
        return if (document.areas!=null) {
            GeneralStatistic(StatisticsBuilder().getWordsStatistic(document),
                StatisticsBuilder().getPageStatistic(document))
        } else GeneralStatistic(StatisticsBuilder().getWordsStatistic(document))
    }
}
