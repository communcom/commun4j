package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

internal enum class BlacklistType {
    USER, COMMUNITIES;

    override fun toString(): String {
        return when (this) {
            USER -> "users"
            COMMUNITIES -> "communities"
        }
    }
}


//getBlacklist:                    // Получить черный список профиля
//userId <string>              // Идентификатор пользователя
//type <string> [
//users                    // Пользователи, внесенные в черный список
//|   communities              // Сообщества, внесенные в черный список
//]
@JsonClass(generateAdapter = true)
internal data class BlacklistRequest(val userId: CyberName, val type: String)

@JsonClass(generateAdapter = true)
data class BlacklistedUsersResponse(val items: List<BlacklistUserItem>): List<BlacklistUserItem> by items

@JsonClass(generateAdapter = true)
data class BlacklistUserItem(val userId: CyberName, val username: String?, val avatarUrl: String?,
                             val isSubscribed: Boolean?)

@JsonClass(generateAdapter = true)
data class BlacklistedCommunitiesResponse(val items: List<BlacklistCommunityItem>): List<BlacklistCommunityItem>  by items

@JsonClass(generateAdapter = true)
data class BlacklistCommunityItem(val communityId: String, val alias: String?, val name: String?,
                              val isSubscribed: Boolean?, val avatarUrl: String?)