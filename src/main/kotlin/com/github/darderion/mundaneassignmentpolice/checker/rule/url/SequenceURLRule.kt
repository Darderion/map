package com.github.darderion.mundaneassignmentpolice.checker.rule.url

import com.github.darderion.mundaneassignmentpolice.checker.RuleViolation

infix fun URLRule.then(next: URLRule): URLRule {
    if (next.area != this.area) throw IllegalArgumentException("Areas must be equal")
    return SequenceURLRule(listOf(this, next))
}

private class SequenceURLRule(
    val sequenceOfRules: List<URLRule>
) : URLRule(
    sequenceOfRules.map { it.predicates }.flatten(),
    sequenceOfRules.first().type,
    sequenceOfRules.first().area,
    sequenceOfRules.first().name
) {
    override fun getRuleViolations(urls: List<Url>): List<Pair<Url, RuleViolation>> {
        val rulesViolations = mutableSetOf<Pair<Url, RuleViolation>>()
        val urlsToCheck = urls.toMutableList()

        sequenceOfRules.forEach { rule ->
            val violations = rule.getRuleViolations(urlsToCheck)
            rulesViolations.addAll(violations)
            urlsToCheck -= violations.map { it.first }.toSet()
        }

        return rulesViolations.toList()
    }
}
