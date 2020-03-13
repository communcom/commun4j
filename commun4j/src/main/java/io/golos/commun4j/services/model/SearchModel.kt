package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.model.*
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.utils.ToStringParseable
import java.util.*

enum class SearchableEntities {
    POSTS, PROFILES, COMMENTS, COMMUNITIES, ALL;

    override fun toString(): String {
        return this.name.toLowerCase()
    }
}

@JsonClass(generateAdapter = true)
internal data class QuickSearchRequest(val queryString: String, val limit: Int?, val entities: List<String>?)

@JsonClass(generateAdapter = true)
data class QuickSearchResponse(val items: List<QuickSearchResponseItem>, val total: Int) : List<QuickSearchResponseItem> by items

sealed class QuickSearchResponseItem(open val type: String)

@JsonClass(generateAdapter = true)
data class QuickSearchCommunityItem(val subscribersCount: Int,
                                    val leadersCount: Int?,
                                    val postsCount: Int?,
                                    val communityId: String,
                                    val issuer: CyberName?,
                                    val alias: String?,
                                    val name: String,
                                    val registrationTime: Date?,
                                    val avatarUrl: String?,
                                    val coverUrl: String?,
                                    val language: String?,
                                    val isSubscribed: Boolean?,
                                    val isBlocked: Boolean?,
                                    val isLeader: Boolean?,
                                    val isStoppedLeader: Boolean?,
                                    override val type: String) : QuickSearchResponseItem(type)


@JsonClass(generateAdapter = true)
data class QuickSearchProfileItem(val username: String?,
                                  val subscriptions: GetProfileResult.UserSubscriptions?,
                                  val subscribers: GetProfileResult.Subscribers?,
                                  val stats: GetProfileResult.UserStats?,
                                  val leaderIn: List<String>,
                                  val userId: CyberName,
                                  val registration: GetProfileResult.UserRegistration?,
                                  val personal: GetProfileResult.Personal?,
                                  val isSubscribed: Boolean?,
                                  val isSubscription: Boolean?,
                                  val isBlocked: Boolean?,
                                  val avatarUrl: String?,
                                  val coverUrl: String?,
                                  override val type: String) : QuickSearchResponseItem(type)

@JsonClass(generateAdapter = true)
data class QuickSearchCommentItem(val votes: DiscussionVotes,
                                  val meta: DiscussionMetadata,
                                  val childCommentsCount: Int,
                                  val isDeleted: Boolean?,
                                  val contentId: DiscussionId,
                                  val parents: CyberCommentParent,
                                  @ToStringParseable val document: String?,
                                  val author: DiscussionAuthor,
                                  val community: CyberCommunity,
                                  override val type: String) : QuickSearchResponseItem(type)

@JsonClass(generateAdapter = true)
data class QuickSearchPostItem(
        @ToStringParseable
        val document: String?,
        val votes: DiscussionVotes,
        val meta: DiscussionMetadata,
        val contentId: DiscussionId,
        val isDeleted: Boolean?,
        val author: DiscussionAuthor,
        val reports: CyberDiscussionReports?,
        val community: CyberCommunity,
        val url: String?,
        val stats: DiscussionStats?,
        val textLength: Int?,
        override val type: String) : QuickSearchResponseItem(type)

@JsonClass(generateAdapter = true)
internal data class ExtendedSearchRequest(val queryString: String, val entities: ExtendedSearchRequestEntities)

@JsonClass(generateAdapter = true)
data class ExtendedSearchResponse(val profiles: ExtendedSearchResponsePart?,
                                  val communities: ExtendedSearchResponsePart?,
                                  val posts: ExtendedSearchResponsePart?)

@JsonClass(generateAdapter = true)
internal data class ExtendedSearchRequestEntities(val profiles: ExtendedRequestSearchItem?,
                                                  val communities: ExtendedRequestSearchItem?,
                                                  val posts: ExtendedRequestSearchItem?)

@JsonClass(generateAdapter = true)
data class ExtendedRequestSearchItem(val limit: Int?, val offset: Int?)


@JsonClass(generateAdapter = true)
data class ExtendedSearchResponsePart(val items: List<QuickSearchResponseItem>, val total: Int) : List<QuickSearchResponseItem> by items
