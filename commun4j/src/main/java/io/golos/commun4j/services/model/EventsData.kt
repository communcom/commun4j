package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.model.DiscussionId
import io.golos.commun4j.sharedmodel.CyberName
import java.util.*

@JsonClass(generateAdapter = true)
class EventsData(val total: Int,
                 val totalByTypes: TotalByTypes,
                 val fresh: Int,
                 val freshByTypes: TotalByTypes,
                 val unread: Int,
                 val unreadByTypes: TotalByTypes,
                 val data: List<Event>)

@JsonClass(generateAdapter = true)
class TotalByTypes(val summary: Int,
                   val transfer: Int?,
                   val reply: Int?,
                   val subscribe: Int?,
                   val unsubscribe: Int?,
                   val mention: Int?,
                   val repost: Int?,
                   val reward: Int?,
                   val curatorReward: Int?,
                   val message: Int?,
                   val witnessVote: Int?,
                   val witnessCancelVote: Int?)

sealed class Event(val eventType: EventType,
                   val _id: String,
                   val fresh: Boolean,
                   val unread: Boolean,
                   val timestamp: Date)

class VoteEvent(val actor: Actor,
                val post: Post?,
                val comment: Comment?,
                val community: CyberCommunity,
                _id: String,
                fresh: Boolean,
                unread: Boolean,
                timestamp: Date) : Event(EventType.VOTE, _id, fresh, unread, timestamp)

class FlagEvent(val actor: Actor,
                val post: Post?,
                val comment: Comment?,
                val community: CyberCommunity,
                _id: String,
                fresh: Boolean,
                unread: Boolean,
                timestamp: Date) : Event(EventType.FLAG, _id, fresh, unread, timestamp)

class TransferEvent(val value: Value,
                    val actor: Actor,
                    _id: String,
                    fresh: Boolean,
                    unread: Boolean,
                    timestamp: Date) : Event(EventType.TRANSFER, _id, fresh, unread, timestamp)

class SubscribeEvent(val community: CyberCommunity,
                     val actor: Actor,
                     _id: String,
                     fresh: Boolean,
                     unread: Boolean,
                     timestamp: Date) : Event(EventType.SUBSCRIBE, _id, fresh, unread, timestamp)

class UnSubscribeEvent(val community: CyberCommunity,
                       val actor: Actor,
                       _id: String,
                       fresh: Boolean,
                       unread: Boolean,
                       timestamp: Date) : Event(EventType.UN_SUBSCRIBE, _id, fresh, unread, timestamp)

class ReplyEvent(val comment: Comment,
                 val post: Post?,
                 val parentComment: Comment?,
                 val community: CyberCommunity,
                 val actor: Actor,
                 _id: String,
                 fresh: Boolean,
                 unread: Boolean,
                 timestamp: Date) : Event(EventType.REPLY, _id, fresh, unread, timestamp)

class MentionEvent(val comment: Comment?,
                   val post: Post?,
                   val parentComment: Comment?,
                   val community: CyberCommunity,
                   val actor: Actor,
                   _id: String,
                   fresh: Boolean,
                   unread: Boolean,
                   timestamp: Date) : Event(EventType.MENTION, _id, fresh, unread, timestamp)

class RepostEvent(val post: Post,
                  val comment: Comment?,
                  val community: CyberCommunity,
                  val actor: Actor,
                  _id: String,
                  fresh: Boolean,
                  unread: Boolean,
                  timestamp: Date) : Event(EventType.REPOST, _id, fresh, unread, timestamp)

class AwardEvent(val payout: Value,
                 _id: String,
                 fresh: Boolean,
                 unread: Boolean,
                 timestamp: Date) : Event(EventType.REWARD, _id, fresh, unread, timestamp)

class CuratorAwardEvent(val post: Post?,
                        val comment: Comment?,
                        val payout: Value,
                        val community: CyberCommunity,
                        val actor: Actor,
                        _id: String,
                        fresh: Boolean,
                        unread: Boolean,
                        timestamp: Date) : Event(EventType.CURATOR_REWARD, _id, fresh, unread, timestamp)


class WitnessVoteEvent(val actor: Actor,
                       _id: String,
                       fresh: Boolean,
                       unread: Boolean,
                       timestamp: Date) : Event(EventType.WITNESS_VOTE, _id, fresh, unread, timestamp)

class WitnessCancelVoteEvent(val actor: Actor,
                             _id: String,
                             fresh: Boolean,
                             unread: Boolean,
                             timestamp: Date) : Event(EventType.WITNESS_CANCEL_VOTE, _id, fresh, unread, timestamp)


class Value(val amount: Double, val currency: String)

class Actor(val userId: CyberName,
            val username: String?,
            val avatarUrl: String?)

class Post(val contentId: DiscussionId, val title: String?)

class Comment(val contentId: DiscussionId, val body: String)

@JsonClass(generateAdapter = true)
class EventJson(
        val eventType: EventType,
        val _id: String,
        val fresh: Boolean,
        val unread: Boolean,
        val timestamp: Date,
        val community: CyberCommunity? = null,
        val value: Value? = null,
        val payout: Value? = null,
        val actor: Actor? = null,
        val post: Post? = null,
        val comment: Comment? = null,
        val parentComment: Comment? = null)