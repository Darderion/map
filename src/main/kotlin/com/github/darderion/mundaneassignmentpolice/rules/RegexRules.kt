package com.github.darderion.mundaneassignmentpolice.rules

import com.github.darderion.mundaneassignmentpolice.checker.rulebuilder.regexbuilder.RegexRuleBuilder
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFArea
import com.github.darderion.mundaneassignmentpolice.pdfdocument.PDFRegion


val RULE_ORDER_OF_REFERENCES = RegexRuleBuilder<RegexRuleBuilder<*>>()
        .called("Неверный порядок ссылок на литературу")
        .regex(Regex("""\[[0-9,\s]+\]"""))
        .searchIn(1)
        .disallow { matches ->
            matches.filter { pair ->
                val references = pair.first
                val referencesInIntList = references
                        .slice(IntRange(1, references.length - 2))
                        .split(Regex(""","""))
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .map { it.toInt() }
                referencesInIntList != referencesInIntList.sorted()
            }.map { it.second }
        }.getRule()

val RULE_VARIOUS_ABBREVIATIONS = RegexRuleBuilder<RegexRuleBuilder<*>>()
        .called("Использованы различные версии сокращения")
        .regex(Regex("""[a-zA-Zа-яА-Я]+"""))
        .inArea(PDFRegion.EVERYWHERE.except(PDFArea.BIBLIOGRAPHY))
        .disallow { matches ->
            val abbreviations = hashSetOf<String>()
            val allWords = hashMapOf<String, HashSet<String>>()
            matches.forEach { pair ->
                val word = pair.first
                if (word.slice(IntRange(1, word.length - 1))
                                .count { it.isUpperCase() } > 0)
                    abbreviations.add(word.uppercase())
                if (!allWords.containsKey(word.lowercase()))
                    allWords.put(word.lowercase(), hashSetOf())
                allWords[word.lowercase()]?.add(word)
            }
            matches.filter { pair ->
                val word = pair.first
                if (abbreviations.contains(word.uppercase()))
                    allWords[word.lowercase()]?.size!! > 1
                else
                    false
            }.map { it.second }
        }.getRule()