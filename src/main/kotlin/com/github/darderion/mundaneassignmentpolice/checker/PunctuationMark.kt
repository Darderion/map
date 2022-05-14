package com.github.darderion.mundaneassignmentpolice.checker

enum class PunctuationMark(val value: Char) {
    FULL_STOP('.'),
    COMMA(',')
}

fun Char.isPunctuationMark() = PunctuationMark.values().map { it.value }.contains(this)

fun String.isPunctuationMark() = this.length == 1 && this.single().isPunctuationMark()
