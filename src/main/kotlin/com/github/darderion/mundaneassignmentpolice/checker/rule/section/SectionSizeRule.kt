package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.ComparisonType
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.SectionName
import com.github.darderion.mundaneassignmentpolice.checker.SectionName.BIBLIOGRAPHY
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.statisticsservice.statistics.StatisticsBuilder
import com.github.darderion.mundaneassignmentpolice.utils.floatEquals

class SectionSizeRule(
    name: String,
    type: RuleViolationType,
    val sections: List<SectionName>,
    val comparisonType: ComparisonType,
    val pageLimit: Int?,
    val percentageLimit: Number?
) : Rule(PDFRegion.EVERYWHERE, name, type) {
    init {
        if (pageLimit == null && percentageLimit == null) throw Exception("Size limit was not specified")
    }

    override fun process(document: PDFDocument): List<RuleViolation> {
        val statisticsBuilder = StatisticsBuilder()
        val ruleViolations = mutableListOf<RuleViolation>()

        if (sections.contains(BIBLIOGRAPHY)) {
            val bibliographyStatistics = statisticsBuilder.getBibliographyStatistics(document)

            if (isLimitViolated(bibliographyStatistics.areaSizeInPage, bibliographyStatistics.areaSizeInPercents)) {
                val titleLine = document.text.first { it.area == PDFArea.BIBLIOGRAPHY }
                ruleViolations.add(RuleViolation(listOf(titleLine), name, type))
            }
        }

        val sectionsStatistic = statisticsBuilder.getSectionsStatistics(document).map { statistics ->
            statistics to SectionName.getByTitle(statistics.section.title)
        }.filter { sections.contains(it.second) }.map { it.first }

        sectionsStatistic.forEach { statistics ->
            if (isLimitViolated(statistics.areaSizeInPage, statistics.areaSizeInPercents)) {
                val titleLines = document.getLines(statistics.section.titleIndex, statistics.section.contentIndex - 1)
                ruleViolations.add(RuleViolation(titleLines, name, type))
            }
        }

        return ruleViolations
    }

    private fun isLimitViolated(sectionSize: Int, sectionPercentage: Float): Boolean {
        var isViolatedPageLimit = false
        var isViolatedPercentageLimit = false

        pageLimit?.let { limit -> isViolatedPageLimit = !comparisonType.compare(sectionSize, limit) }
        percentageLimit?.let { limit ->
            isViolatedPercentageLimit = !comparisonType.compare(sectionPercentage, limit.toFloat(), ::floatEquals)
        }

        return isViolatedPageLimit || isViolatedPercentageLimit
    }
}
