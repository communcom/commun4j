package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ProposalsRequest(val communityIds: List<String>?,
                            val sequenceKey: String?,
                            val limit: Int?)