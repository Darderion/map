package com.github.darderion.mundaneassignmentpolice.checker.rule.word

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

class PredicateWordRule(
    word: String,
    private val ruleBody: (document: PDFDocument, line: Int, index: Int) -> Boolean,
    type: RuleViolationType,
    area: PDFRegion,
    name: String
) : WordRule(word, type, area, name) {
    override fun isViolated(document: PDFDocument, line: Int, index: Int): Boolean {
        return ruleBody(document, line, index)
    }
}