package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section

enum class SectionTitle(val text: String) {
    Introduction("Введение"),
    ProblemStatement("Постановка задачи"),
    Overview("Обзор"),
    Conclusion("Заключение")
}

abstract class SectionRule(
    name: String,
    type: RuleViolationType,
    val title: SectionTitle
): Rule(PDFRegion.NOWHERE.except(PDFArea.SECTION), name, type) {
    abstract fun isViolated(section: Section, document: PDFDocument): Boolean

    override fun process(document: PDFDocument): List<RuleViolation> {
        val section = document.areas!!.sections.firstOrNull {
            it.title.contains(title.text, true)
        } ?: return emptyList()

        if (!isViolated(section, document)) return emptyList()

        return listOf(RuleViolation(listOf(document.text[section.titleIndex]), name, type))
    }
}
