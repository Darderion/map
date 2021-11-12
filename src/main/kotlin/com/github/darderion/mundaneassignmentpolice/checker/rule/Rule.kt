package com.github.darderion.mundaneassignmentpolice.checker.rule

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

abstract class Rule(
	val area: PDFRegion,
	protected val name: String
) {
	abstract fun process(document: PDFDocument): List<RuleViolation>
}
