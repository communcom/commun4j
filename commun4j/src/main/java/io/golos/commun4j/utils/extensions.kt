package io.golos.commun4j.utils

import io.golos.commun4j.sharedmodel.CyberName


internal fun checkArgument(assertion: Boolean, errorMessage: String) {
    if (!assertion) throw IllegalArgumentException(errorMessage)
}

fun String.toCyberName() = CyberName(this)