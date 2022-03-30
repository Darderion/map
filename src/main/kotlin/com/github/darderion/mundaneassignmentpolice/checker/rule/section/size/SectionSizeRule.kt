package com.github.darderion.mundaneassignmentpolice.checker.rule.section.size

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.section.SectionName
import com.github.darderion.mundaneassignmentpolice.checker.rule.section.SectionRule
import com.github.darderion.mundaneassignmentpolice.checker.rule.section.size.ComparisonType.*
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section
import com.github.darderion.mundaneassignmentpolice.utils.floatEquals
import com.github.darderion.mundaneassignmentpolice.utils.greaterOrEqual
import com.github.darderion.mundaneassignmentpolice.utils.lessOrEqual

class SectionSizeRule(
    name: String,
    type: RuleViolationType,
    sectionName: SectionName,
    val comparisonType: ComparisonType,
    val pageLimit: Int?,
    val percentageLimit: Number?
) : SectionRule(name, type, sectionName) {
    init {
        if (pageLimit == null && percentageLimit == null) throw Exception("Size limit was not specified")
    }

    override fun isViolated(section: Section, document: PDFDocument): Boolean {
        val text = document.text
        val sections = document.areas!!.sections

        val sectionIndex = sections.indexOf(section)
        val numbering = section.title.takeWhile { it.isDigit() || it == '.' }
        val subsections = if (numbering.isNotEmpty()) {
            sections.drop(sectionIndex + 1).takeWhile {
                it.title.startsWith(numbering)
            }
        } else emptyList()

        val lastPageOfSections = text[text.indexOfFirst { it.area == PDFArea.BIBLIOGRAPHY } - 1].page

        val firstSectionPage = text[section.titleIndex].page
        val lastSectionPage = when {
            subsections.isNotEmpty() && subsections.last() == sections.last()
                || section == sections.last() -> lastPageOfSections
            else -> {
                val nextSection = sections[sectionIndex + subsections.size + 1]
                text[nextSection.titleIndex - 1].page
            }
        }

        val totalPages = text.last().page + 1
        val sectionSize = lastSectionPage - firstSectionPage + 1
        return isLimitViolated(sectionSize, totalPages)
    }

    private fun isLimitViolated(sectionSize: Int, totalPages: Int): Boolean {
        var isViolatedPageLimit = false
        var isViolatedPercentageLimit = false
        val percentage = sectionSize.toFloat() / totalPages * 100

        when (comparisonType) {
            LESS_THAN -> {
                pageLimit?.let { isViolatedPageLimit = sectionSize >= it }
                percentageLimit?.toFloat()?.let { isViolatedPercentageLimit = percentage.greaterOrEqual(it) }
            }
            LESS_THAN_OR_EQUAL -> {
                pageLimit?.let { isViolatedPageLimit = sectionSize > it }
                percentageLimit?.toFloat()?.let { isViolatedPercentageLimit = percentage > it }
            }
            EQUAL -> {
                pageLimit?.let { isViolatedPageLimit = sectionSize != it }
                percentageLimit?.toFloat()?.let { isViolatedPercentageLimit = !floatEquals(percentage, it) }
            }
            NOT_EQUAL -> {
                pageLimit?.let { isViolatedPageLimit = sectionSize == it }
                percentageLimit?.toFloat()?.let { isViolatedPercentageLimit = floatEquals(percentage, it) }
            }
            GREATER_THAN_OR_EQUAL -> {
                pageLimit?.let { isViolatedPageLimit = sectionSize < it }
                percentageLimit?.toFloat()?.let { isViolatedPercentageLimit = percentage < it }
            }
            GREATER_THAN -> {
                pageLimit?.let { isViolatedPageLimit = sectionSize <= it }
                percentageLimit?.toFloat()?.let { isViolatedPercentageLimit = percentage.lessOrEqual(it) }
            }
        }
        return isViolatedPageLimit || isViolatedPercentageLimit
    }
}
