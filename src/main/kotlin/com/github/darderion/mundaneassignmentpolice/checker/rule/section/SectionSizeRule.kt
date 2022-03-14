package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.section.ComparisonType.*
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section
import com.github.darderion.mundaneassignmentpolice.utils.floatEquals

enum class ComparisonType {
    LESS_THAN, LESS_THAN_OR_EQUAL_TO, EQUAL_TO, GREATER_THAN_OR_EQUAL_TO, GREATER_THAN
}

class SectionSizeRule(
    name: String,
    type: RuleViolationType,
    title: SectionTitle,
    val comparisonType: ComparisonType,
    val pageLimit: Int?,
    val percentageLimit: Number?
): SectionRule(name, type, title) {
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

        val firstPageOfSections = text[sections.first().titleIndex].page
        val lastPageOfSections = text[text.indexOfFirst { it.area == PDFArea.BIBLIOGRAPHY } - 1].page

        val firstSectionPage = text[section.titleIndex].page
        val lastSectionPage = when {
            subsections.isNotEmpty() && subsections.last() == sections.last()
                || section == sections.last()  -> lastPageOfSections
            else -> {
                val nextSection = sections[sectionIndex + subsections.size + 1]
                text[nextSection.titleIndex - 1].page
            }
        }

        val totalPagesOfSections = lastPageOfSections - firstPageOfSections + 1
        val sectionSize = lastSectionPage - firstSectionPage + 1
        return isViolated(sectionSize, totalPagesOfSections)
    }

    private fun isViolated(sectionSize: Int, totalPages: Int): Boolean {
        var isViolatedPageLimit = false
        var isViolatedPercentageLimit = false
        val percentage = sectionSize.toDouble() / totalPages * 100

        when(comparisonType) {
            LESS_THAN -> {
                pageLimit?.let { isViolatedPageLimit = sectionSize >= it }
                percentageLimit?.let { isViolatedPercentageLimit = percentage >= it.toDouble() }
            }
            LESS_THAN_OR_EQUAL_TO -> {
                pageLimit?.let { isViolatedPageLimit = sectionSize > it }
                percentageLimit?.let { isViolatedPercentageLimit = percentage > it.toDouble() }
            }
            EQUAL_TO -> {
                pageLimit?.let { isViolatedPageLimit = sectionSize != it }
                percentageLimit?.let { isViolatedPercentageLimit = !floatEquals(percentage, it.toFloat()) }
            }
            GREATER_THAN_OR_EQUAL_TO -> {
                pageLimit?.let { isViolatedPageLimit = sectionSize < it }
                percentageLimit?.let { isViolatedPercentageLimit = percentage < it.toDouble() }
            }
            GREATER_THAN -> {
                pageLimit?.let { isViolatedPageLimit = sectionSize <= it }
                percentageLimit?.let { isViolatedPercentageLimit = percentage <= it.toDouble() }
            }
        }
        if (pageLimit != null && percentageLimit != null)
            return isViolatedPageLimit && isViolatedPercentageLimit

        return isViolatedPageLimit || isViolatedPercentageLimit
    }
}
