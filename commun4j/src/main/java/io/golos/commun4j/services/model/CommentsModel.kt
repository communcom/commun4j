package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.model.*
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.utils.ToStringParseable

//sortBy <string>('time')                 // Способ сортировки
//[
//time                              // Сначала старые, потом новые
//| timeDesc                          // Сначала новые, потом старые
//| popularity                        // По популярности (сначала -- с наибольшим количеством upvote)
//]
enum class CommentsSortBy {
    TIME, TIME_DESC, POPULARITY;

    override fun toString(): String {
        return when (this) {
            TIME -> "time"
            TIME_DESC -> "timeDesc"
            POPULARITY -> "popularity"
        }
    }
}

//type <string>('post')                   // Тип ленты
//[
//user                              // Получить комментарии пользователя, требует userId
//| post                              // Получить комментарии для поста или родительского комментария. Если у комменария вложенности 1 менее 5 детей, они также участвуют в выдаче
//| replies                           // Получить комментарии, которые были оставлены пользователю, требует userId
//]
enum class CommentsSortType {
    USER, POST, REPLIES;

    override fun toString(): String {
        return when (this) {
            USER -> "user"
            POST -> "post"
            REPLIES -> "replies"
        }
    }
}
@JsonClass(generateAdapter = true)
data class ParentComment(val userId: CyberName, val permlink: String)

@JsonClass(generateAdapter = true)
internal data class GetCommentRequest(val userId: String, val communityId: String, val permlink: String)

@JsonClass(generateAdapter = true)
internal data class GetCommentsRequest(val sortBy: String?, val offset: Int?, val limit: Int?, val type: String?,
                              val userId: String?, val permlink: String?, val communityId: String?,
                              val communityAlias: String?, val parentComment: ParentComment?, val resolveNestedComments: Boolean?)

@JsonClass(generateAdapter = true)
data class GetCommentsResponse(val items: List<CyberComment>): List<CyberComment> by items

@JsonClass(generateAdapter = true)
data class GetCommentsResponseRaw(val items: List<CyberCommentRaw>)

@JsonClass(generateAdapter = true)
data class CyberComment(val votes: DiscussionVotes, val meta: DiscussionMetadata, val childCommentsCount: Int,
                        val isDeleted: Boolean?,
                        val contentId: DiscussionId, val parents: CyberCommentParent, val document: CyberDiscussionContent?,
                        val author: DiscussionAuthor,  val community: CyberCommunity, val type: String)

@JsonClass(generateAdapter = true)
data class CyberCommentRaw(val votes: DiscussionVotes, val meta: DiscussionMetadata, val childCommentsCount: Int,
                           val isDeleted: Boolean?,
                           val contentId: DiscussionId, val parents: CyberCommentParent,
                           @ToStringParseable val document: String?,
                           val author: DiscussionAuthor,  val community: CyberCommunity, val type: String)

@JsonClass(generateAdapter = true)
data class CyberCommentParent(val post: DiscussionId? , val comment: DiscussionId?)