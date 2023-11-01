package com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section

data class SectionStatistics(val section: Section, val sectionSizeInPage: Int,
                             val sectionSizeInPercents: Float, val documentSize:Int)

