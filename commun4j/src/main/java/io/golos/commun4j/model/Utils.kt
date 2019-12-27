package io.golos.commun4j.model

internal fun <T> T?.asList(): List<T> = if (this == null) emptyList() else listOf(this)