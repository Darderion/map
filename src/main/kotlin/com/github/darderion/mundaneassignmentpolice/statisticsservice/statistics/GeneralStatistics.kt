package com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics

import com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics.area.BibliographyStatistics
import com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics.area.SectionStatistics

data class WordsStatistic(
    val wordCount: Int,
    val topKWords: Map<String,Int>
)
data class PageStatistic(
    val lineCount: Int,
    val sectionsStatistics: List<SectionStatistics>,
    val bibliographyStatistics: BibliographyStatistics
)
class GeneralStatistic(
    val wordsStatistic: WordsStatistic,
    val pageStatistic: PageStatistic?=null
)

