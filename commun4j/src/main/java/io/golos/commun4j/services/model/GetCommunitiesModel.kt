package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName
import java.util.*

enum class CommunitiesRequestType {
    ALL, USER;

    override fun toString(): String {
        return when (this) {
            ALL -> "all"
            USER -> "user"
        }
    }
}

@JsonClass(generateAdapter = true)
internal data class GetCommunitiesRequest(val type: String?, val userId: String?, val search: String?, val offset: Int?, val limit: Int?, val allowedLanguages: List<String>?)

@JsonClass(generateAdapter = true)
data class GetCommunitiesResponse(val items: List<GetCommunitiesItem>) : List<GetCommunitiesItem> by items

@JsonClass(generateAdapter = true)
internal data class GetCommunityRequest(val communityId: String?, val communityAlias: String?)

@JsonClass(generateAdapter = true)
data class GetCommunitiesItem(val subscribersCount: Int,
                              val communityId: String,
                              val alias: String?,
                              val issuer: CyberName?,
                              val name: String,
                              val avatarUrl: String?,
                              val coverUrl: String?,
                              val leadersCount: Int?,
                              val language: String?,
                              val registrationTime: Date?,
                              val rules: List<CommunityRuleItem>?,
                              val description: String?,
                              val postsCount: Int?,
                              val friendsCount: Int?,
                              val isSubscribed: Boolean?,
                              val isBlocked: Boolean?,
                              val friends: List<CommunityFriendItem>?,
                              val isInBlacklist: Boolean?,
                              val isLeader: Boolean?,
                              val isStoppedLeader: Boolean?)

@JsonClass(generateAdapter = true)
data class CommunityRuleItem(val _id: String?, val id: String, val title: String?, val text: String?)

@JsonClass(generateAdapter = true)
data class CommunityFriendItem(val userId: CyberName, val username: String?, val avatarUrl: String?)