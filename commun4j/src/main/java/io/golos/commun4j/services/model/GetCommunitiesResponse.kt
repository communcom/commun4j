package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class GetCommunitiesResponse(val items: List<GetCommunitiesItem>)

@JsonClass(generateAdapter = true)
data class GetCommunitiesItem(val subscribersCount: Int,
                              val communityId: String,
                              val alias: String?,
                              val name:String,
                              val avatarUrl: String?,
                              val coverImageLink: String?,
                              val language: String?,
                              val description: String?,
                              val postsCount: Int?,
                              val isSubscribed: Boolean?,
                              val isBlocked: Boolean?)