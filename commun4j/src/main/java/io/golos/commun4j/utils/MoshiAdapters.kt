package io.golos.commun4j.utils

import com.squareup.moshi.*
import io.golos.commun4j.http.rpc.model.contract.response.AbiAction
import io.golos.commun4j.services.model.*


class UserRegistrationStateAdapter : JsonAdapter<UserRegistrationState>() {

    override fun fromJson(reader: JsonReader): UserRegistrationState {
        val value = reader.nextString()

        return when (value) {
            "registered" -> UserRegistrationState.REGISTERED
            "firstStep" -> UserRegistrationState.FIRST_STEP
            "verify" -> UserRegistrationState.VERIFY
            "setUsername" -> UserRegistrationState.SET_USER_NAME
            "toBlockChain" -> UserRegistrationState.TO_BLOCK_CHAIN
            "createIdentity" -> UserRegistrationState.CREATE_IDENTITY
            else -> throw IllegalArgumentException("unknown step $value")
        }
    }

    override fun toJson(writer: JsonWriter, value: UserRegistrationState?) {
        writer.value(value?.name ?: "")
    }
}

annotation class ToStringParseable

class ToStringAdapter {

    @FromJson
    @ToStringParseable
    fun fromJson(reader: JsonReader): String? {
        val token = reader.peek()
        if (token == JsonReader.Token.STRING) return reader.nextString()

        val data = reader.readJsonValue() ?: return null
        return moshi.adapter(Map::class.java).toJson(data as Map<*, *>)
    }

    companion object {

        private val moshi = Moshi.Builder().build()
    }
}

class UserRegistrationStrategyAdapter : JsonAdapter<RegistrationStrategy>() {

    override fun fromJson(reader: JsonReader): RegistrationStrategy {
        val value = reader.nextString()

        return when (value) {
            "smsFromUser" -> RegistrationStrategy.SMS_FROM_USER
            "smsToUser" -> RegistrationStrategy.SMS_TO_USER
            "mail" -> RegistrationStrategy.MAIL
            "social" -> RegistrationStrategy.SOCIAL
            else -> throw IllegalArgumentException("unknown strategy $value")
        }
    }

    override fun toJson(writer: JsonWriter, value: RegistrationStrategy?) {
        writer.value(value?.name ?: "")
    }
}

class EventTypeAdapter : JsonAdapter<EventType>() {
    override fun fromJson(reader: JsonReader): EventType? {
        val value = reader.nextString()
        return when (value) {
            "upvote" -> EventType.VOTE
            "transfer" -> EventType.TRANSFER
            "reply" -> EventType.REPLY
            "downvote" -> EventType.FLAG
            "subscribe" -> EventType.SUBSCRIBE
            "unsubscribe" -> EventType.UN_SUBSCRIBE
            "mention" -> EventType.MENTION
            "repost" -> EventType.REPOST
            "reward" -> EventType.REWARD
            "curatorReward" -> EventType.CURATOR_REWARD
            "witnessVote" -> EventType.WITNESS_VOTE
            "witnessCancelVote" -> EventType.WITNESS_CANCEL_VOTE
            else -> throw java.lang.IllegalArgumentException("unknown type $value")
        }
    }

    override fun toJson(writer: JsonWriter, value: EventType?) {
        writer.value(value?.toString())
    }
}

class ServiceSettingsLanguageAdapter : JsonAdapter<ServiceSettingsLanguage>() {
    override fun fromJson(reader: JsonReader): ServiceSettingsLanguage? {
        val langString = reader.nextString()
        return when (langString) {
            "ru" -> ServiceSettingsLanguage.RUSSIAN
            "en" -> ServiceSettingsLanguage.ENGLISH
            else -> throw  java.lang.IllegalArgumentException("unknown language $langString")
        }
    }

    override fun toJson(writer: JsonWriter, value: ServiceSettingsLanguage?) {
        writer.value(value?.toString())
    }
}

class EventsAdapter {
    @FromJson
    fun fromJson(eventsJson: EventJson): Event = when (eventsJson.eventType) {
        EventType.VOTE -> VoteEvent(
                eventsJson.actor!!,
                eventsJson.post,
                eventsJson.comment,
                eventsJson.community!!,
                eventsJson._id,
                eventsJson.fresh,
                eventsJson.unread,
                eventsJson.timestamp
        )

        EventType.FLAG -> FlagEvent(
                eventsJson.actor!!,
                eventsJson.post,
                eventsJson.comment,
                eventsJson.community!!,
                eventsJson._id,
                eventsJson.fresh,
                eventsJson.unread,
                eventsJson.timestamp
        )
        EventType.TRANSFER -> TransferEvent(
                eventsJson.value!!,
                eventsJson.actor!!, eventsJson._id, eventsJson.fresh, eventsJson.unread, eventsJson.timestamp
        )

        EventType.SUBSCRIBE -> SubscribeEvent(
                eventsJson.community!!,
                eventsJson.actor!!,
                eventsJson._id,
                eventsJson.fresh,
                eventsJson.unread,
                eventsJson.timestamp
        )
        EventType.UN_SUBSCRIBE -> UnSubscribeEvent(
                eventsJson.community!!,
                eventsJson.actor!!,
                eventsJson._id,
                eventsJson.fresh,
                eventsJson.unread,
                eventsJson.timestamp
        )

        EventType.REPLY -> ReplyEvent(
                eventsJson.comment!!,
                eventsJson.post,
                eventsJson.parentComment,
                eventsJson.community!!,
                eventsJson.actor!!,
                eventsJson._id,
                eventsJson.fresh,
                eventsJson.unread,
                eventsJson.timestamp
        )

        EventType.MENTION -> MentionEvent(
                eventsJson.comment,
                eventsJson.post,
                eventsJson.parentComment,
                eventsJson.community!!,
                eventsJson.actor!!,
                eventsJson._id,
                eventsJson.fresh,
                eventsJson.unread,
                eventsJson.timestamp
        )

        EventType.REPOST -> RepostEvent(
                eventsJson.post!!,
                eventsJson.comment, eventsJson.community!!,
                eventsJson.actor!!, eventsJson._id, eventsJson.fresh, eventsJson.unread, eventsJson.timestamp
        )

        EventType.REWARD -> AwardEvent(
                eventsJson.payout!!,
                eventsJson._id,
                eventsJson.fresh,
                eventsJson.unread,
                eventsJson.timestamp
        )

        EventType.CURATOR_REWARD -> CuratorAwardEvent(
                eventsJson.post, eventsJson.comment,
                eventsJson.value!!, eventsJson.community!!,
                eventsJson.actor!!, eventsJson._id, eventsJson.fresh, eventsJson.unread, eventsJson.timestamp
        )

        EventType.WITNESS_VOTE -> WitnessVoteEvent(
                eventsJson.actor!!,
                eventsJson._id,
                eventsJson.fresh,
                eventsJson.unread,
                eventsJson.timestamp
        )
        EventType.WITNESS_CANCEL_VOTE -> WitnessCancelVoteEvent(
                eventsJson.actor!!,
                eventsJson._id,
                eventsJson.fresh,
                eventsJson.unread,
                eventsJson.timestamp
        )

    }

    @ToJson
    fun toJson(event: Event): EventJson = when (event) {
        is WitnessCancelVoteEvent -> EventJson(
                EventType.WITNESS_CANCEL_VOTE,
                event._id, event.fresh, event.unread, event.timestamp
        )

        is VoteEvent -> EventJson(
                EventType.VOTE, event._id, event.fresh, event.unread, event.timestamp,
                actor = event.actor, post = event.post, comment = event.comment
        )

        is FlagEvent -> EventJson(
                EventType.FLAG, event._id, event.fresh, event.unread, event.timestamp,
                actor = event.actor, post = event.post, comment = event.comment
        )

        is TransferEvent -> EventJson(
                EventType.TRANSFER, event._id, event.fresh, event.unread, event.timestamp,
                value = event.value, actor = event.actor
        )

        is SubscribeEvent -> EventJson(
                EventType.SUBSCRIBE, event._id, event.fresh, event.unread, event.timestamp,
                community = event.community, actor = event.actor
        )

        is UnSubscribeEvent -> EventJson(
                EventType.UN_SUBSCRIBE, event._id, event.fresh, event.unread, event.timestamp,
                community = event.community, actor = event.actor
        )

        is ReplyEvent -> EventJson(
                EventType.REPLY, event._id, event.fresh, event.unread, event.timestamp,
                post = event.post, comment = event.comment, community = event.community,
                actor = event.actor
        )

        is MentionEvent -> EventJson(
                EventType.MENTION, event._id, event.fresh, event.unread, event.timestamp,
                post = event.post, comment = event.comment, community = event.community,
                actor = event.actor
        )

        is RepostEvent -> EventJson(
                EventType.REPOST, event._id, event.fresh, event.unread, event.timestamp,
                post = event.post, comment = event.comment, community = event.community,
                actor = event.actor
        )

        is AwardEvent -> EventJson(
                EventType.REWARD, event._id, event.fresh, event.unread, event.timestamp,
                payout = event.payout
        )

        is CuratorAwardEvent -> EventJson(
                EventType.CURATOR_REWARD, event._id, event.fresh, event.unread, event.timestamp,
                post = event.post, comment = event.comment, payout = event.payout
        )

        is WitnessVoteEvent -> EventJson(
                EventType.WITNESS_VOTE,
                event._id, event.fresh, event.unread, event.timestamp
        )
    }
}