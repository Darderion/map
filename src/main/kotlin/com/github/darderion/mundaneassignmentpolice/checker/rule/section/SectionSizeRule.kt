package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.NOWHERE
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section

class SectionSizeRule(
    name: String,
    type: RuleViolationType,
    val sectionName: String,
    val pageLimit: Int,
    val percentageLimit: Int
): Rule(NOWHERE.except(PDFArea.SECTION), name, type) {
    private fun isViolated(section: Section, document: PDFDocument): Boolean {
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
                || section == sections.last()  -> lastPageOfSections
            else -> {
                val nextSection = sections[sectionIndex + subsections.size + 1]
                text[nextSection.titleIndex - 1].page
            }
        }

        val totalPages = text.last().page + 1
        val sectionSize = lastSectionPage - firstSectionPage + 1
        return sectionSize > pageLimit || sectionSize.toDouble() / totalPages * 100 > percentageLimit
    }

    override fun process(document: PDFDocument): List<RuleViolation> {
        val section = document.areas!!.sections.firstOrNull {
            it.title.contains(sectionName, true)
        } ?: return emptyList()

        if (!isViolated(section, document)) return emptyList()

        return listOf(RuleViolation(listOf(document.text[section.titleIndex]), name, type))
    }
}