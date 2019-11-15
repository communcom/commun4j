package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName


@JsonClass(generateAdapter = true)
data class GetCommunitiesResponse(val items: List<GetCommunitiesItem>)

@JsonClass(generateAdapter = true)
data class GetCommunitiesItem(val subscribersCount: Int,
                              val communityId: String,
                              val alias: String?,
                              val name: String,
                              val avatarUrl: String?,
                              val coverUrl: String?,
                              val leadersCount: Int?,
                              val language: String?,
                              val rules: Any?,
                              val description: String?,
                              val postsCount: Int?,
                              val friendsCount: Int?,
                              val isSubscribed: Boolean?,
                              val isBlocked: Boolean?,
                              val friends: List<GetCommunitiesItemFriendItem>?)

@JsonClass(generateAdapter = true)
data class GetCommunitiesItemFriendItem(val userId: CyberName, val username: String?, val avatarUrl: String?)