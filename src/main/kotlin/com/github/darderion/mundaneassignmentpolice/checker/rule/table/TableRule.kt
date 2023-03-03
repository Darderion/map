package com.github.darderion.mundaneassignmentpolice.checker.rule.table

import com.github.darderion.mundaneassignmentpolice.checker.RuleTableViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.checker.rule.Rule
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion
import com.github.darderion.mundaneassignmentpolice.pdfdocument.tables.Table
import com.github.darderion.mundaneassignmentpolice.pdfdocument.tables.Cell

class TableRule (
    val predicates: MutableList<(Table) -> List<Cell>>,
    val type: RuleViolationType,
    val area: PDFRegion,
    val name: String
    ){
       fun process(document: PDFDocument): List<RuleTableViolation> {
            val rulesTablesViolations: MutableSet<RuleTableViolation> = mutableSetOf()

            predicates.forEach { predicate ->
                rulesTablesViolations.addAll(
                    document.tables.map {
                        predicate(it)
                    }.filter { it.isNotEmpty() }.map {
                        RuleTableViolation(it, name, type)
                    }
                )
            }

            return rulesTablesViolations.toList()
        }
    }
