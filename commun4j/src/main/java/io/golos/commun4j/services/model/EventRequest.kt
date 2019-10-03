package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class MarkAsReadRequest(val ids: List<String>, val app: String)

@JsonClass(generateAdapter = true)
internal class MarkAllReadRequest(val app: String)

@JsonClass(generateAdapter = true)
internal class GetUnreadCountRequest(val profile: String, val app: String)

@JsonClass(generateAdapter = true)
class FreshResult(val fresh: Int)