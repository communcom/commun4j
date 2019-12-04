package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

@JsonClass(generateAdapter = true)
internal data class SuggestNameRequest(val text: String)

@JsonClass(generateAdapter = true)
data class SuggestNameResponse(val items: List<SuggestNameItem>): List<SuggestNameItem> by items

@JsonClass(generateAdapter = true)
data class SuggestNameItem(val userId: CyberName, val username: String?, val avatarUrl: String?)
