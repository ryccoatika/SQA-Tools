package com.ryccoatika.sqatools.common.utils

fun Long?.orZero(): Long = this ?: 0

fun Double?.orZero(): Double = this ?: 0.0

fun String?.or(default: String): String = this?.takeIf { it.isNotBlank() } ?: default
