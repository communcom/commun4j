package io.golos.commun4j.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.services.model.CyberCommunity
import io.golos.commun4j.sharedmodel.CyberName
import java.math.BigInteger
import java.util.*

@JsonClass(generateAdapter = true)
data class CyberDiscussion(
        val contentId: DiscussionId,
        val author: DiscussionAuthor?,
        val community: CyberCommunity?,
        val content: DiscussionContent,
        val votes: DiscussionVotes,
        val stats: DiscussionStats?,
        val payout: DiscussionPayout,
        val parent: Parent?,
        val meta: DiscussionMetadata
)

@JsonClass(generateAdapter = true)
data class DiscussionAuthor(val userId: CyberName, val username: String?, val avatarUrl: String?)

@JsonClass(generateAdapter = true)
data class DiscussionId(
        val userId: String,
        val permlink: String
)

@JsonClass(generateAdapter = true)
data class DiscussionStats(val commentsCount: Long?,
                           val rShares: BigInteger?,
                           val hot: Double?,
                           val trending: Double?,
                           val viewCount: Long?)

@JsonClass(generateAdapter = true)
data class DiscussionWilson(val hot: Double, val trending: Double)

@JsonClass(generateAdapter = true)
data class DiscussionContent(val title: String?,
                             val body: ContentBody,
                             val tags: List<String>?,
                             val embeds: List<Embed>)

@JsonClass(generateAdapter = true)
data class ContentBody(
        val preview: String?,
        val full: String?,
        val raw: String?,
        val mobile: List<ContentRow>?,
        val mobilePreview: List<ContentRow>?
)

@JsonClass(generateAdapter = true)
data class Embed(val _id: String?,
                 val id: String?,
                 val type: String?,
                 val result: EmbedResult?)

@JsonClass(generateAdapter = true)
data class EmbedResult(val type: String?,
                       val version: String?,
                       val title: String?,
                       val url: String?,
                       val author: String?,
                       val author_url: String?,
                       val provider_name: String?,
                       val description: String?,
                       val thumbnail_url: String?,
                       val thumbnail_width: Int?,
                       val thumbnail_height: Int?,
                       val height: Int?,
                       val html: String?)

@JsonClass(generateAdapter = true)
data class DiscussionMetadata(val time: Date)

@JsonClass(generateAdapter = true)
data class DiscussionPayout(val author: Payout?, val curator: Payout?,
                            val benefactor: Payout?, val done: Boolean, val meta: PayoutMeta?)

@JsonClass(generateAdapter = true)
data class PayoutMeta(val rewardWeight: String?, val sharesFn: String?, val sumCuratorSw: String?,
                      val benefactorPercents: List<Any>?, val tokenProp: String?, val curatorsPercent: String?)

@JsonClass(generateAdapter = true)
data class Payout(val token: PayoutAmount?, val vesting: PayoutAmount?)

@JsonClass(generateAdapter = true)
data class PayoutAmount(val name: String?, val value: String?)

@JsonClass(generateAdapter = true)
data class DiscussionVotes(
        val hasUpVote: Boolean,
        val hasDownVote: Boolean,
        val upCount: Int,
        val downCount: Int
)

sealed class ContentRow

data class TextRow(val content: String,
                   val type: String = typeName) : ContentRow() {
    companion object {
        const val typeName = "text"
    }
}


data class ImageRow(val src: String,
                    val type: String = typeName) : ContentRow() {
    companion object {
        const val typeName = "image"
    }
}

@JsonClass(generateAdapter = true)
data class Parent(val post: ParentContentId?, val comment: ParentContentId?)

@JsonClass(generateAdapter = true)
data class ParentContentId(val contentId: DiscussionId)