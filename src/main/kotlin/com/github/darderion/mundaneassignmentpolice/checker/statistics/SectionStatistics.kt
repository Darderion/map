package com.github.darderion.mundaneassignmentpolice.checker.statistics
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section

data class SectionStatistics(var section: Section, var sectionSizeInPage: Int,
                             var sectionSizeInPercents: Float, var documentSize:Int)

