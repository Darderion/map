package com.github.darderion.mundaneassignmentpolice.checker

enum class ComparisonType {
    LESS_THAN {
        override fun <T : Comparable<T>> compare(first: T, second: T, equals: (T, T) -> Boolean) =
            first < second
    },

    LESS_THAN_OR_EQUAL {
        override fun <T : Comparable<T>> compare(first: T, second: T, equals: (T, T) -> Boolean) =
            first < second || equals(first, second)
    },

    EQUAL {
        override fun <T : Comparable<T>> compare(first: T, second: T, equals: (T, T) -> Boolean) =
            equals(first, second)
    },

    NOT_EQUAL {
        override fun <T : Comparable<T>> compare(first: T, second: T, equals: (T, T) -> Boolean) =
            !equals(first, second)
    },

    GREATER_THAN_OR_EQUAL {
        override fun <T : Comparable<T>> compare(first: T, second: T, equals: (T, T) -> Boolean) =
            first > second || equals(first, second)
    },

    GREATER_THAN {
        override fun <T : Comparable<T>> compare(first: T, second: T, equals: (T, T) -> Boolean) =
            first > second
    };

    abstract fun <T : Comparable<T>> compare(
        first: T,
        second: T,
        equals: (T, T) -> Boolean = { a, b -> a == b }
    ): Boolean
}
