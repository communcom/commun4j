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
                            val commonCommunitiesCount: Int?,
                            val commonCommunities: List<CommunityItem>?) {

    @JsonClass(generateAdapter = true)
    data class Personal(val avatarUrl: String?, val biography: String?, val contacts: Contacts?, val coverUrl: String?)

    @JsonClass(generateAdapter = true)
    data class Contacts(val facebook: String?, val telegram: String?, val weChat: String?, val whatsApp: String?, val vkontakte: String?,
                        val instagram: String)

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
}

