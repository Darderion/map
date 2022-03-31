package com.github.darderion.mundaneassignmentpolice.checker.statistics

data class Statistic (val wordsStatistic: WordsStatistic,val pageStatistic: PageStatistic? =null)

data class WordsStatistic(
    val wordCount: Int,
    val topKWords: Map<String,Int>
)
data class PageStatistic(
    val lineCount: Int,
    val pageStatistic: MutableList<SectionStatistics>
)
