package io.golos.commun4j.services.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class CommentsRequest(val sortBy: String?,
                               val sequenceKey: String?,
                               val limit: Int?,
                               @Json(name = "contentType")
                               val contentType: String?,
                               val type: String?,
                               val userId: String?,
                               val permlink: String?,
                               val username: String?,
                               val app: String)