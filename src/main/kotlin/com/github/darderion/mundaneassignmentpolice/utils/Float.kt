package com.github.darderion.mundaneassignmentpolice.utils

import kotlin.math.abs

fun floatEquals(a: Float, b: Float) = abs(a - b) < 0.0000001
fun floatEquals(a: Float, b: Double) = abs(a - b) < 0.001
fun floatEquals(a: Double, b: Float) = abs(a - b) < 0.001

infix fun Float.nearby(other: Float) = abs(this - other) < 0.2

infix fun Float.lessOrEqual(other: Float) = this < other || floatEquals(this, other)
infix fun Float.greaterOrEqual(other: Float) = this > other || floatEquals(this, other)
