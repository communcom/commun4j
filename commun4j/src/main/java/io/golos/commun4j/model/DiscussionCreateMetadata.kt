package io.golos.commun4j.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiscussionCreateMetadata
@JvmOverloads
constructor(var embeds: List<EmbedmentsUrl> = emptyList())

@JsonClass(generateAdapter = true)
data class EmbedmentsUrl(var url: String)
