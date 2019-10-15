package io.golos.commun4j.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.services.model.CyberCommunity
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.utils.ToStringParseable
import java.math.BigInteger
import java.util.*

@JsonClass(generateAdapter = true)
data class GetDiscussionsResult(val items: List<CyberDiscussion>)

data class GetDiscussionsResultRaw(val items: List<CyberDiscussionRaw>)

@JsonClass(generateAdapter = true)
data class CyberDiscussion(val content: CyberDiscussionContent,
                           val votes: DiscussionVotes,
                           val meta: DiscussionMetadata,
                           val contentId: DiscussionId,
                           val author: DiscussionAuthor,
                           val community: CyberCommunity)

@JsonClass(generateAdapter = true)
data class CyberDiscussionRaw(
        @ToStringParseable
        val content: String,
        val votes: DiscussionVotes,
        val meta: DiscussionMetadata,
        val contentId: DiscussionId,
        val author: DiscussionAuthor,
        val community: CyberCommunity)

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
data class CyberDiscussionContent(
        val attributes: CyberAttributes,
        val id: Long,
        val type: String,
        val content: List<Content>)

@JsonClass(generateAdapter = true)
data class CyberAttributes(val type: String,
                           val version: Double,
                           val title: String)


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
data class DiscussionMetadata(val creationTime: Date)

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
        val upCount: Long,
        val downCount: Long
)

sealed class Content(id: Long, type: String)

data class Paragraph(val id: Long, val type: String, val content: List<ParagraphContent>) : Content(id, type)

@JsonClass(generateAdapter = true)
data class ParagraphContent(val id: Long, val type: String, val content: String)


data class Attachments(val id: Long, val type: String, val content: List<AttachmentsContent>) : Content(id, type)

@JsonClass(generateAdapter = true)
data class AttachmentsContent(val id: Long, val type: String, val content: String,
                              val attributes: AttachmentsAttributes?)

@JsonClass(generateAdapter = true)
data class AttachmentsAttributes(val title: String?, val url: String?, val author: String?,
                                 val author_url: String?, val provider_name: String?,
                                 val description: String?, val thumbnail_url: String?,
                                 val thumbnail_width: Int?, val thumbnail_height: Int?,
                                 val html: String?)

@JsonClass(generateAdapter = true)
data class Parent(val post: ParentContentId?, val comment: ParentContentId?)

@JsonClass(generateAdapter = true)
data class ParentContentId(val contentId: DiscussionId)