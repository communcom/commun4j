package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class HashTagTopRequest(val communityId: String,         // Идентификатор комьюнити
                                      val limit: Int?,           // Количество элементов
                                      val sequenceKey: String?)