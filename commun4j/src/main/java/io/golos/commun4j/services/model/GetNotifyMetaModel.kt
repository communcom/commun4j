package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

//getNotifyMeta:                // Получение мета-данных для отображения нотификации
//userId <string>           // Получить данные пользователя по id
//communityId <string>      // Получить данные комьюнити по идентификатору
//postId:                   // Получить данные поста по идентификатору
//userId <string>       // Id пользователя-автора
//permlink <string>     // Пермлинк контента
//commentId:                // Получить данные комментария по идентификатору
//userId <string>       // Id пользователя-автора
//permlink <string>     // Пермлинк контента
//contentId:                // Получить данные поста/комментария по идентификатору
//userId <string>       // Id пользователя-автора
//permlink <string>     // Пермлинк контента


@JsonClass(generateAdapter = true)
internal data class GetNotifyMetaRequest(val userId: CyberName?,val communityId: String?,
                                         val postId: GetNotifyMetaPostId?,val commentId: GetNotifyMetaCommentId?,
                                         val contentId: GetNotifyMetaContentId?)

@JsonClass(generateAdapter = true)
data class GetNotifyMetaPostId(val userId: CyberName, val permlink: String)

@JsonClass(generateAdapter = true)
data class GetNotifyMetaCommentId(val userId: CyberName, val permlink: String)

@JsonClass(generateAdapter = true)
data class GetNotifyMetaContentId(val userId: CyberName, val permlink: String)
