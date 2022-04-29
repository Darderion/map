package com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics.area
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section

class SectionStatistics(
    val section: Section,
    sectionSizeInPage: Int,
    documentSize: Int
): AreaStatistics(PDFArea.SECTION, sectionSizeInPage, documentSize)
