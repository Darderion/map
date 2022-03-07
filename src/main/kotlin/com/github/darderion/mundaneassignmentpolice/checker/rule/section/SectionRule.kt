package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.NOWHERE
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section

class SectionRule(
    val predicates: List<(sections: List<Section>) -> List<Section>>,
    type: RuleViolationType,
    name: String
): Rule(NOWHERE.except(PDFArea.SECTION), name, type) {
    override fun process(document: PDFDocument): List<RuleViolation> {
        val ruleViolations: MutableSet<RuleViolation> = mutableSetOf()
        val sections = document.areas!!.sections
        predicates.forEach { predicate ->
                predicate(sections)
                    .map {document.text.slice(it.titleIndex until it.contentIndex) }
                    .map { RuleViolation(it, name, type) }
                    .forEach { ruleViolations.add(it) }
        }
        return ruleViolations.toList()
    }
}