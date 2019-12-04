package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

internal enum class SubscriptionType {
    USER, COMMUNITY;

    override fun toString(): String {
        return when (this) {
            USER -> "user"
            COMMUNITY -> "community"
        }
    }
}

@JsonClass(generateAdapter = true)
internal class SubscriptionsRequest(val userId: CyberName,
                                    val type: String,
                                    val limit: Int?,
                                    val offset: Int?)

@JsonClass(generateAdapter = true)
data class UserSubscriptionsResponse(val items: List<UserSubscriptionItem>): List<UserSubscriptionItem> by items

@JsonClass(generateAdapter = true)
data class UserSubscriptionItem(
        val userId: CyberName,
        val username: String?,
        val avatarUrl: String?,
        val isSubscribed: Boolean?,
        val subscribersCount: Int?,
        val postsCount: Int?)

@JsonClass(generateAdapter = true)
data class CommunitySubscriptionsResponse(val items: List<CommunitySubscriptionItem>): List<CommunitySubscriptionItem> by items

@JsonClass(generateAdapter = true)
data class CommunitySubscriptionItem(val communityId: String, val alias: String?, val name: String?,
                                     val isSubscribed: Boolean?, val code: String?)
