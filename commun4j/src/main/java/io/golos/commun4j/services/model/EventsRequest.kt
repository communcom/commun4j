package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class EventsRequest(
        val profile: String,
        val app: String,
        val afterId: String?,
        //from 1 to 100
        val limit: Int?,
        val types: List<EventType>?,
        val markAsViewed: Boolean?,
        val freshOnly: Boolean?) {
}

enum class EventType {
    VOTE, FLAG, TRANSFER, REPLY, SUBSCRIBE, UN_SUBSCRIBE,
    MENTION, REPOST, REWARD, CURATOR_REWARD, WITNESS_VOTE,
    WITNESS_CANCEL_VOTE;

    override fun toString(): String {
        return when (this) {
            VOTE -> "upvote"
            TRANSFER -> "transfer"
            REPLY -> "reply"
            FLAG -> "downvote"
            SUBSCRIBE -> "subscribe"
            UN_SUBSCRIBE -> "unsubscribe"
            MENTION -> "mention"
            REPOST -> "repost"
            REWARD -> "reward"
            CURATOR_REWARD -> "curatorReward"
            WITNESS_VOTE -> "witnessVote"
            WITNESS_CANCEL_VOTE -> "witnessCancelVote"
        }
    }

}