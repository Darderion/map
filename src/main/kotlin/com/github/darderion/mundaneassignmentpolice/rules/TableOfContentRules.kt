package com.github.darderion.mundaneassignmentpolice.rules

import com.github.darderion.mundaneassignmentpolice.checker.Section
import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.tableofcontentbuilder.TableOfContentRuleBuilder
import java.util.HashSet


val RULE_TABLE_OF_CONTENT_NUMBERS = TableOfContentRuleBuilder<TableOfContentRuleBuilder<*>>()
        .called("Введение, заключение и список литературы не нумеруются")
        .disallow {
            it.filter {
                // println("${it.text.count()} -> ${it.content}")
                val text = it.text.filter { it.text.trim().isNotEmpty() }
                ((text.count() == 3 && (text[1].text == Section.INTRODUCTION.title ||
                        text[1].text == Section.CONCLUSION.title)) ||
                        (text.count() == 4 && (text[1].text + " " + text[2].text) == Section.BIBLIOGRAPHY.title))
            }
        }
        .getRule()


val RULE_SYMBOLS_IN_SECTION_NAMES = TableOfContentRuleBuilder<TableOfContentRuleBuilder<*>>()
        .disallow { listOfLines ->
            listOfLines.filter { line ->
                val text = line.text.filterNot { it.text == "." }           // remove leaders
                        .filterNot { it.text.contains("[0-9]+\\.".toRegex()) }  // remove numbering
                        .joinToString("")
                text.contains("[:.,]".toRegex())
            }
        }.called("""Символы ":", ".", "," в названии секции""")
        .getRule()

val sectionsThatMayPrecedeThis = mapOf<String, HashSet<String>>(
        Section.INTRODUCTION.title to hashSetOf(""),
        Section.PROBLEM_STATEMENT.title to hashSetOf(Section.INTRODUCTION.title),
        Section.REVIEW.title to hashSetOf(Section.PROBLEM_STATEMENT.title),
        Section.CONTENT.title to hashSetOf(Section.REVIEW.title, Section.CONTENT.title),
        Section.CONCLUSION.title to hashSetOf(Section.CONTENT.title),
        Section.BIBLIOGRAPHY.title to hashSetOf(Section.CONCLUSION.title)
)

val RULE_SECTIONS_ORDER = TableOfContentRuleBuilder<TableOfContentRuleBuilder<*>>()
        .disallow { listOfLines ->
            var nameOfPreviousSection = ""
            listOfLines
                    .filterNot { line ->
                        val words = line.text
                                .filter { it.text.trim().isNotEmpty() }
                                .filterNot { it.text.contains("[0-9]+\\.".toRegex()) }		// remove numbering
                        words.isEmpty() || words[0].text == Section.TABLE_OF_CONTENT.title
                    }
                    .filter { line ->
                        val words = line.text
                                .filter { it.text.trim().isNotEmpty() }
                                .filterNot { it.text.contains("[0-9]+\\.".toRegex()) }		// remove numbering

                        val sectionName =
                                if ((words[0].text + " " + words[1].text) == Section.BIBLIOGRAPHY.title ||
                                        (words[0].text + " " + words[1].text) == Section.PROBLEM_STATEMENT.title
                                )
                                    words[0].text + " " + words[1].text
                                else if (sectionsThatMayPrecedeThis.contains(words[0].text))
                                    words[0].text
                                else
                                    Section.CONTENT.title

                        val isRuleViolation =
                                !sectionsThatMayPrecedeThis[sectionName]!!.contains(nameOfPreviousSection)
                        nameOfPreviousSection = sectionName
                        isRuleViolation
                    }
        }
        .called("Неверный порядок секций")
        .getRule()