package com.github.darderion.mundaneassignmentpolice.checker.rule.sentence
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Line
import com.github.darderion.mundaneassignmentpolice.pdfdocument.text.Word

fun splitIntoSentences(lines : MutableList<Line>, separator: String = ".!?”»:;"): MutableList<MutableList<Word>> {
    val punctuationConclusionSymbols = separator.toCharArray()
    val sentences = mutableListOf<MutableList<Word>>()
    var sentence = mutableListOf<Word>()

    lines.forEach { line ->
        line.text.filter { it.text.length > 1 && it.text.isNotEmpty()}
                .forEach { sentence.add(it)
                    if (sentence.first().text.first().isUpperCase() &&
                            sentence.last().text.last() in punctuationConclusionSymbols) {
                        sentences.add(sentence)
                        sentence = mutableListOf()
                    }
                    else if (sentence.last().text.last() in punctuationConclusionSymbols)
                        sentence = mutableListOf()
                }
    }
    return sentences
}