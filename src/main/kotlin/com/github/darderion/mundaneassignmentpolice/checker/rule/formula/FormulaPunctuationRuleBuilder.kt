package com.github.darderion.mundaneassignmentpolice.checker.rule.formula

import com.github.darderion.mundaneassignmentpolice.checker.PunctuationMark
import com.github.darderion.mundaneassignmentpolice.checker.RuleViolationType

class FormulaPunctuationRuleBuilder {
    private var type: RuleViolationType = RuleViolationType.Error
    private var name: String = "Rule name"
    private var punctuationMark: PunctuationMark = PunctuationMark.FULL_STOP
    private var indicatorWords: MutableList<Regex> = mutableListOf()
    private var ignoredWords: MutableList<Regex> = mutableListOf()

    infix fun called(name: String) = this.also { this.name = name }

    infix fun type(type: RuleViolationType) = this.also { this.type = type }

    infix fun requiredPunctuationMark(punctuationMark: PunctuationMark) = this.also {
        this.punctuationMark = punctuationMark
    }

    fun indicatorWords(vararg words: Regex) = this.also { indicatorWords.addAll(words.toList()) }

    fun ignoredWords(vararg symbols: Regex) = this.also { ignoredWords.addAll(symbols.toList()) }

    fun getRule() = FormulaPunctuationRule(
        type,
        name,
        punctuationMark,
        indicatorWords,
        ignoredWords
    ) as FormulaRule
}
