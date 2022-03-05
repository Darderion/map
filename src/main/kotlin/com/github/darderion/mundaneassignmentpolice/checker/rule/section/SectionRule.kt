package com.github.darderion.mundaneassignmentpolice.checker.rule.section

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion.Companion.NOWHERE
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Section

class SectionRule(
    val predicates: List<(sections: List<Section>) -> List<Line>>,
    type: RuleViolationType,
    name: String
): Rule(NOWHERE.except(PDFArea.SECTION), name, type) {
    override fun process(document: PDFDocument): List<RuleViolation> {
        val ruleViolations: MutableSet<RuleViolation> = mutableSetOf()

        return ruleViolations.toList()
    }
}