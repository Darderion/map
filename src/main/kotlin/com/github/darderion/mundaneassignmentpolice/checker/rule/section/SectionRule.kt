package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section

abstract class SectionRule(
    name: String,
    type: RuleViolationType,
    val sectionName: SectionName
) : Rule(PDFRegion.EVERYWHERE, name, type) {
    abstract fun isViolated(section: Section, document: PDFDocument): Boolean

    override fun process(document: PDFDocument): List<RuleViolation> {
        val section = document.areas!!.sections.firstOrNull {
            it.title.contains(sectionName.title, true)
        } ?: return emptyList()

        if (!isViolated(section, document)) return emptyList()

        val sectionTitle = document.getLines(section.titleIndex, section.contentIndex - 1)
        return listOf(RuleViolation(sectionTitle, name, type))
    }
}
