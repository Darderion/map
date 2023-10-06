package com.github.darderion.mundaneassignmentpolice.checker.rule.word

import com.github.darderion.mundaneassignmentpolice.checker.Direction
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFDocument
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

class PredicateWordRuleBuilder {
    private var word: String = " "
    private var ruleBody = { document: PDFDocument, line: Int, index: Int -> false }
    private var type: RuleViolationType = RuleViolationType.Error
    private var name: String = "Rule name"
    private var region: PDFRegion = PDFRegion.EVERYWHERE

    infix fun word(word: String) = this.also { this.word = word }

    infix fun called(name: String) = this.also { this.name = name }

    infix fun inArea(area: PDFArea) = this.also { region = PDFRegion.NOWHERE.except(area) }

    infix fun inArea(region: PDFRegion) = this.also { this.region = region }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    fun setRuleBody(deliveredRuleBody: ( document: PDFDocument, line: Int, index: Int) -> Boolean) =
        this.also { this.ruleBody = deliveredRuleBody }
    fun getRule() = PredicateWordRule(
        word,
        ruleBody,
        type,
        region,
        name
    ) as WordRule
}