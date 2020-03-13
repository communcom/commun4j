package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class GetConfigRequest

@JsonClass(generateAdapter = true)
data class GetConfigResponse(val ftueCommunityBonus: Int, val domain: String, val features: Map<String, Boolean>?)