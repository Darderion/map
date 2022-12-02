package com.github.darderion.mundaneassignmentpolice.rules

import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.listbuilder.ListRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

val RULE_SINGLE_SUBSECTION = ListRuleBuilder<ListRuleBuilder<*>>()
        .inArea(PDFRegion.NOWHERE.except(PDFArea.TABLE_OF_CONTENT))
        //.called("Only 1 subsection in a section")
        .called("Одна подсекция в секции")
        .disallow {
            if (it.nodes.count() == 1) it.nodes.first().getText() else listOf()
        }.getRule()
