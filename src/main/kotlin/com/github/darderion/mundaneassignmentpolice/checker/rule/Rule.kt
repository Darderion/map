package com.github.darderion.mundaneassignmentpolice.checker.rule

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

abstract class Rule(
	val area: PDFRegion,
	val name: String,
	val type: RuleViolationType
) {
	abstract fun process(document: PDFDocument): List<RuleViolation>
}
