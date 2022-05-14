package com.github.darderion.mundaneassignmentpolice.checker.rule.formula

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Formula
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Word

class FormulaPunctuationRuleBuilder {
    private var type: RuleViolationType = RuleViolationType.Error
    private var name: String = "Rule name"
    private var ignoredWords: MutableList<Regex> = mutableListOf()
    private var ruleBody: (formula: Formula, filteredText: List<Word>, nextFormula: Formula?) -> List<Line> =
        { _, _, _ -> emptyList() }

    infix fun called(name: String) = this.also { this.name = name }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    fun ignoredWords(vararg regexes: Regex) = this.also { ignoredWords.addAll(regexes) }

    infix fun rule(
        ruleBody: (formula: Formula, filteredText: List<Word>, nextFormula: Formula?) -> List<Line>
    ) = this.also { this.ruleBody = ruleBody }

    fun getRule() = FormulaPunctuationRule(
        type,
        name,
        ignoredWords,
        ruleBody
    ) as FormulaRule
}
