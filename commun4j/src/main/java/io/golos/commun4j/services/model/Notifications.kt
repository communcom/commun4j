package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberAsset
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import java.math.BigInteger
import java.util.*

enum class GetNotificationsFilter {
    SUBSCRIBE, UPVOTE, REPLY, MENTION;

    override fun toString(): String {
        return when (this) {
            SUBSCRIBE -> "subscribe"
            UPVOTE -> "upvote"
            REPLY -> "reply"
            MENTION -> "mention"
        }
    }
}

@JsonClass(generateAdapter = true)
internal data class GetNotificationsRequest(val limit: Int?, val beforeThan: String?, val filter: List<String>?)

@JsonClass(generateAdapter = true)
internal class GetNotificationsStatusRequest

@JsonClass(generateAdapter = true)
internal class MarkAllNotificationsAsViewedRequest(val until: String)

@JsonClass(generateAdapter = true)
data class GetNotificationStatusResponse(val unseenCount: Int)

@JsonClass(generateAdapter = true)
data class GetNotificationsResponse(val items: List<Notification>, val lastNotificationTimestamp: String?) : List<Notification> by items

@JsonClass(generateAdapter = true)
internal data class GetNotificationsRaw(val items: List<Any>, val lastNotificationTimestamp: String?) : List<Any> by items

sealed class Notification(eventType: String, id: String)

@JsonClass(generateAdapter = true)
data class NotificationUserDescription(val userId: CyberName,
                                       val username: String?,
                                       val avatarUrl: String?)

@JsonClass(generateAdapter = true)
data class NotificationCommunityDescription(val communityId: CyberSymbolCode,
                                            val name: String?,
                                            val alias: String?,
                                            val avatarUrl: String?)

@JsonClass(generateAdapter = true)
data class SubscribeNotification(val eventType: String,
                                 val id: String,
                                 val timestamp: Date,
                                 val userId: CyberName,
                                 val user: NotificationUserDescription,
                                 val isNew: Boolean) : Notification(eventType, id)

@JsonClass(generateAdapter = true)
data class NotificationContentId(val communityId: CyberSymbolCode?,
                                 val userId: CyberName,
                                 val permlink: String,
                                 val username: String?)

@JsonClass(generateAdapter = true)
data class NotificationEntityContent(val shortText: String?,
                                     val imageUrl: String?,
                                     val contentId: NotificationContentId,
                                     val parents: NotificationEntityContentParents?)

@JsonClass(generateAdapter = true)
data class NotificationEntityContentParents(val post: NotificationContentId?, val comment: NotificationContentId?)


@JsonClass(generateAdapter = true)
data class UpvoteNotification(
        val eventType: String,
        val id: String,
        val timestamp: Date,
        val community: NotificationCommunityDescription?,
        val userId: CyberName,
        val voter: NotificationUserDescription?,
        val entityType: String,
        val post: NotificationEntityContent?,
        val comment: NotificationEntityContent?,
        val isNew: Boolean) : Notification(eventType, id)

@JsonClass(generateAdapter = true)
data class MentionNotification(val eventType: String,
                               val id: String,
                               val timestamp: Date,
                               val community: NotificationCommunityDescription?,
                               val userId: CyberName,
                               val author: NotificationUserDescription,
                               val entityType: String,
                               val post: NotificationEntityContent?,
                               val comment: NotificationEntityContent?,
                               val isNew: Boolean) : Notification(eventType, id)

@JsonClass(generateAdapter = true)
data class ReplyNotification(val eventType: String,
                             val id: String,
                             val timestamp: Date,
                             val community: NotificationCommunityDescription?,
                             val userId: CyberName,
                             val author: NotificationUserDescription,
                             val entityType: String,
                             val post: NotificationEntityContent?,
                             val comment: NotificationEntityContent?,
                             val isNew: Boolean) : Notification(eventType, id)

@JsonClass(generateAdapter = true)
data class TransferNotification(val eventType: String,
                                val id: String,
                                val timestamp: Date,
                                val from: NotificationUserDescription?,
                                val userId: CyberName,
                                val amount: Double?,
                                val pointType: String?,
                                val isNew: Boolean) : Notification(eventType, id)

@JsonClass(generateAdapter = true)
data class RewardNotification(val eventType: String,
                              val id: String,
                              val timestamp: Date,
                              val community: NotificationCommunityDescription?,
                              val userId: CyberName,
                              val amount: Double,
                              val tracery: BigInteger?,
                              val isNew: Boolean) : Notification(eventType, id)

