package com.github.darderion.mundaneassignmentpolice.utils

import kotlin.math.abs

fun floatEquals(a: Float, b: Float) = abs(a - b) < 0.0000001
fun floatEquals(a: Float, b: Double) = abs(a - b) < 0.0000001
fun floatEquals(a: Double, b: Float) = abs(a - b) < 0.0000001
