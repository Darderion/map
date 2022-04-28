package com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics.area

import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea

class BibliographyStatistics(
    sizeInPage: Int,
    documentSize: Int
) : AreaStatistics(PDFArea.BIBLIOGRAPHY, sizeInPage, documentSize)
