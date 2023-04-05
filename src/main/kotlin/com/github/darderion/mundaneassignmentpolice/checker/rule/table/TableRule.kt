package com.github.darderion.mundaneassignmentpolice.checker.rule.table

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.tables.Table
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line

class TableRule (
    val predicates: MutableList<(Table) -> List<Line>>,
     type: RuleViolationType,
     area: PDFRegion,
     name: String
    ): Rule(area, name, type){
       override fun process(document: PDFDocument): List<RuleViolation> {
            val rulesViolations: MutableSet<RuleViolation> = mutableSetOf()

            predicates.forEach { predicate ->
                rulesViolations.addAll(
                    document.tables.map {
                        predicate(it)
                    }.filter { it.isNotEmpty() }.map {
                        RuleViolation(it, name, type)
                    }
                )
            }

            return rulesViolations.toList()
        }
    }
