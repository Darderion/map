package com.github.darderion.mundaneassignmentpolice.checker.rule.word
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion

class TwoIdenticalWordsRuleBuilder {
    private var word: String = " "
    private var ignoredIndexes: MutableList<Int> = mutableListOf()
    private var neighborhoodSize: Int = 1
    private var numberOfNeighbors: Int = 1
    private var type: RuleViolationType = RuleViolationType.Error
    private var name: String = "Rule name"
    private var region: PDFRegion = PDFRegion.EVERYWHERE
    infix fun word(word: String) = this.also { this.word = word }
    infix fun called(name: String) = this.also { this.name = name }
    infix fun inArea(area: PDFArea) = this.also { region = PDFRegion.NOWHERE.except(area) }
    infix fun inArea(region: PDFRegion) = this.also { this.region = region }
    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    fun getRule() = TwoIdenticalWordsRule(
        word,
        ignoredIndexes,
        neighborhoodSize,
        numberOfNeighbors,
        type,
        region,
        name
    ) as WordRule
}