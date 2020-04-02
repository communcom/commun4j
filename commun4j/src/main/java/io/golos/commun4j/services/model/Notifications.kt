package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import java.math.BigInteger
import java.util.*

enum class GetNotificationsFilter {
    SUBSCRIBE, UPVOTE, REPLY, MENTION, TRANSFER, REWARD, REFERRAL_REG_BONUS, REFERRAL_PURCH_BONUS;

    override fun toString(): String {
        return when (this) {
            SUBSCRIBE -> "subscribe"
            UPVOTE -> "upvote"
            REPLY -> "reply"
            MENTION -> "mention"
            TRANSFER -> "transfer"
            REWARD -> "reward"
            REFERRAL_REG_BONUS -> "referralRegistrationBonus"
            REFERRAL_PURCH_BONUS -> "referralPurchaseBonus"
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

sealed class Notification(open val eventType: String, open val id: String, open val timestamp: Date)

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
data class SubscribeNotification(override val eventType: String,
                                 override val id: String,
                                 override val timestamp: Date,
                                 val userId: CyberName,
                                 val user: NotificationUserDescription,
                                 val isNew: Boolean) : Notification(eventType, id, timestamp)

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
        override val eventType: String,
        override val id: String,
        override val timestamp: Date,
        val community: NotificationCommunityDescription?,
        val userId: CyberName,
        val voter: NotificationUserDescription?,
        val entityType: String,
        val post: NotificationEntityContent?,
        val comment: NotificationEntityContent?,
        val isNew: Boolean) : Notification(eventType, id, timestamp)

@JsonClass(generateAdapter = true)
data class MentionNotification(override val eventType: String,
                               override val id: String,
                               override val timestamp: Date,
                               val community: NotificationCommunityDescription?,
                               val userId: CyberName,
                               val author: NotificationUserDescription,
                               val entityType: String,
                               val post: NotificationEntityContent?,
                               val comment: NotificationEntityContent?,
                               val isNew: Boolean) : Notification(eventType, id, timestamp)

@JsonClass(generateAdapter = true)
data class ReplyNotification(override val eventType: String,
                             override val id: String,
                             override val timestamp: Date,
                             val community: NotificationCommunityDescription?,
                             val userId: CyberName,
                             val author: NotificationUserDescription,
                             val entityType: String,
                             val post: NotificationEntityContent?,
                             val comment: NotificationEntityContent?,
                             val isNew: Boolean) : Notification(eventType, id, timestamp)

@JsonClass(generateAdapter = true)
data class TransferNotification(override val eventType: String,
                                override val id: String,
                                override val timestamp: Date,
                                val from: NotificationUserDescription?,
                                val userId: CyberName,
                                val amount: Double?,
                                val pointType: String?,
                                val community: NotificationCommunityDescription?,
                                val isNew: Boolean) : Notification(eventType, id, timestamp)

@JsonClass(generateAdapter = true)
data class RewardNotification(override val eventType: String,
                              override val id: String,
                              override val timestamp: Date,
                              val community: NotificationCommunityDescription?,
                              val userId: CyberName,
                              val amount: Double,
                              val tracery: BigInteger?,
                              val isNew: Boolean) : Notification(eventType, id, timestamp)

@JsonClass(generateAdapter = true)
data class ReferralRegistrationBonusNotification(override val eventType: String,
                                                 override val id: String,
                                                 override val timestamp: Date,
                                                 val userId: CyberName,
                                                 val from: NotificationUserDescription,
                                                 val amount: Double,
                                                 val pointType: String,
                                                 val isNew: Boolean) : Notification(eventType, id, timestamp)

@JsonClass(generateAdapter = true)
data class ReferralPurchaseBonusNotification(override val eventType: String,
                                             override val id: String,
                                             override val timestamp: Date,
                                             val userId: CyberName,
                                             val from: NotificationUserDescription,
                                             val amount: Double,
                                             val pointType: String,
                                             val percent: Int,
                                             val isNew: Boolean) : Notification(eventType, id, timestamp)

class UnsupportedNotification(eventType: String, id: String, timestamp: Date, val rawData: Map<*, *>) : Notification(eventType, id, timestamp)