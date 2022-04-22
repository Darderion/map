package com.github.darderion.mundaneassignmentpolice.utils.comparator

import com.github.darderion.mundaneassignmentpolice.utils.comparator.ComparisonType.*

class Comparator {
    companion object {
        fun <T : Comparable<T>> compare(
            first: T,
            comparisonType: ComparisonType = EQUAL,
            second: T,
            equals: (T, T) -> Boolean = { a, b -> a == b }
        ) = when (comparisonType) {
            LESS_THAN -> first < second
            LESS_THAN_OR_EQUAL -> first < second || equals(first, second)
            EQUAL -> equals(first, second)
            NOT_EQUAL -> !equals(first, second)
            GREATER_THAN_OR_EQUAL -> first > second || equals(first, second)
            GREATER_THAN -> first > second
        }
    }
}
