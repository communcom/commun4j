package io.golos.commun4j.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.services.model.CyberCommunity
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.utils.ToStringParseable
import java.util.*

@JsonClass(generateAdapter = true)
data class GetDiscussionsResult(val items: List<CyberDiscussion>) : List<CyberDiscussion> by items

data class GetDiscussionsResultRaw(val items: List<CyberDiscussionRaw>) : List<CyberDiscussionRaw> by items

@JsonClass(generateAdapter = true)
data class CyberDiscussion(val document: CyberDiscussionContent?,
                           val votes: DiscussionVotes,
                           val meta: DiscussionMetadata,
                           val contentId: DiscussionId,
                           val author: DiscussionAuthor,
                           val reports: CyberDiscussionReports?,
                           val community: CyberCommunity,
                           val url: String?,
                           val stats: DiscussionStats?,
                           val type: String?,
                           val textLength: Int?)

@JsonClass(generateAdapter = true)
data class CyberDiscussionRaw(
        @ToStringParseable
        val document: String?,
        val votes: DiscussionVotes,
        val meta: DiscussionMetadata,
        val contentId: DiscussionId,
        val author: DiscussionAuthor,
        val reports: CyberDiscussionReports?,
        val community: CyberCommunity,
        val url: String?,
        val stats: DiscussionStats?,
        val type: String?,
        val textLength: Int?)

@JsonClass(generateAdapter = true)
data class CyberDiscussionReports(val reportsCount: Int?)

@JsonClass(generateAdapter = true)
data class DiscussionAuthor(val userId: CyberName, val username: String?, val avatarUrl: String?)

@JsonClass(generateAdapter = true)
data class DiscussionId(
        val userId: CyberName,
        val communityId: String,
        val permlink: String
)

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
                           val title: String?)


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
        val downCount: Long,
        val hasUpVote: Boolean?,
        val hasDownVote: Boolean?
)

sealed class Content(id: Long, type: String)

@JsonClass(generateAdapter = true)
data class Paragraph(val id: Long, val type: String, val content: List<ParagraphContent>) : Content(id, type)

@JsonClass(generateAdapter = true)
data class EmbedContent(val id: Long, val type: String, val content: String?, val attributes: Map<String, Any>) : Content(id, type)

@JsonClass(generateAdapter = true)
data class Website(val id: Long, val type: String, val content: String?) : Content(id, type)


//[{"id":14,"type":"image","content":"https://bloximages.newyork1.vip.townnews.com/omaha.com/content/tncms/assets/v3/editorial/9/ab/9abaecf1-6f92-5bfd-9e9b-2236572ee247/5d0bef05bd0c4.image.jpg"}
@JsonClass(generateAdapter = true)
data class ImageContent(val id: Long?, val type: String, val content: String?) : Content(id
        ?: Long.MAX_VALUE, type)

@JsonClass(generateAdapter = true)
data class VideoContent(val id: Long, val type: String, val content: String?, val attributes: Map<String, Any>?) : Content(id, type)

@JsonClass(generateAdapter = true)
data class ParagraphContent(val id: Long, val type: String, val content: String)

@JsonClass(generateAdapter = true)
data class Attachments(val id: Long, val type: String, val content: List<AttachmentsContent>) : Content(id, type)

@JsonClass(generateAdapter = true)
data class AttachmentsContent(val id: Long, val type: String, val content: String,
                              val attributes: AttachmentsAttributes?)

@JsonClass(generateAdapter = true)
data class DiscussionStats(val commentsCount: Int?)

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