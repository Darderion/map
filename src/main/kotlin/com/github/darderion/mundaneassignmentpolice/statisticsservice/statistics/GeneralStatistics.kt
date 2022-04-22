package com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics

data class WordsStatistic(
    val wordCount: Int,
    val topKWords: Map<String,Int>
)
data class PageStatistic(
    val lineCount: Int,
    val sectionsStatistic: MutableList<SectionStatistics>
)
class GeneralStatistic(
    val wordsStatistic: WordsStatistic,
    val pageStatistic: PageStatistic?=null
)

