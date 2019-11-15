package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

enum class GetCommentVotesRequestType {
    LIKES, DISLIKES;

    override fun toString(): String {
        return when (this) {
            LIKES -> "like"
            DISLIKES -> "dislike"
        }


    }
}

@JsonClass(generateAdapter = true)
internal data class GetCommentVotesRequest(val sequenceKey: String?, // Идентификатор пагинации для получения следующего контента
                                           val limit: Int?,        // Количество элементов
                                           val userId: CyberName,           // Id пользователя
                                           val permlink: String,         // Пермлинк комментария
                                           val type: String)             // Тип запрашиваемых голосов
