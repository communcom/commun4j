package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName
import java.util.*


@JsonClass(generateAdapter = true)
data class GetProfileResult(val username: String?,
                            val subscriptions: UserSubscriptions?,
                            val subscribers: Subscribers?,
                            val stats: UserStats?,
                            val leaderIn: List<String>,
                            val userId: CyberName,
                            val registration: UserRegistration?,
                            val personal: Personal?,
                            val isSubscribed: Boolean?,
                            val isSubscription: Boolean?,
                            val isBlocked: Boolean?,
                            val highlightCommunities: List<ProfileHighlightedCommunitiesItem>?,
                            val highlightCommunitiesCount: Int?,
                            val commonFriendsCount: Int?,
                            val commonFriends: List<ProfileCommonFriendItem>?,
                            val isInBlacklist: Boolean?,
                            val avatarUrl: String?,
                            val coverUrl: String?) {

    @JsonClass(generateAdapter = true)
    data class ProfileHighlightedCommunitiesItem(val communityId: String,
                                                 val alias: String?,
                                                 val name: String?,
                                                 val avatarUrl: String?,
                                                 val coverUrl: String?,
                                                 val postsCount: Int?,
                                                 val isSubscribed: Boolean?,
                                                 val subscribersCount: Int?)

    @JsonClass(generateAdapter = true)
    data class Personal(val biography: String?)

    @JsonClass(generateAdapter = true)
    data class Subscribers(val usersCount: Int,
                           val communitiesCount: Int?)

    @JsonClass(generateAdapter = true)
    data class UserRegistration(val time: Date)

    @JsonClass(generateAdapter = true)
    data class UserStats(val postsCount: Int,
                         val reputation: Long,
                         val commentsCount: Long)

    @JsonClass(generateAdapter = true)
    data class UserSubscriptions(
            val usersCount: Int,
            val communitiesCount: Int)

    @JsonClass(generateAdapter = true)
    data class CommunityItem(val communityId: String, val alias: String?, val name: String?)

    @JsonClass(generateAdapter = true)
    data class ProfileCommonFriendItem(val userId: CyberName, val username: String?, val avatarUrl: String?, val subscribersCount: Int?)
}

