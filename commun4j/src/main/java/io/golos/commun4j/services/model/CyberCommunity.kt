package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CyberCommunity(val communityId: String, val name: String?, val avatarUrl: String?, val alias: String?)