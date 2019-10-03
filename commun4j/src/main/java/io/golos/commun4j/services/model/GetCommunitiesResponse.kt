package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class GetCommunitiesResponse(val communities: List<GetCommunitiesItem>)

@JsonClass(generateAdapter = true)
data class GetCommunitiesItem(val subscribersCount: Int,
                              val communityId: String,
                              val communityName:String,
                              val avatar: String?,
                              val coverImageLink: String?,
                              val tokenName: String,
                              val ticker: String,
                              val description: String?,
                              val rules: String?)