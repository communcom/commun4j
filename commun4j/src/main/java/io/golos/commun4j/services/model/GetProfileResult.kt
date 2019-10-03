package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName
import java.util.*

@JsonClass(generateAdapter = true)
data class GetProfileResult(val username: String?,
                            val subscriptions: UserSubscriptions,
                            val subscribers: Subscribers,
                            val stats: UserStats,
                            val blacklist: List<String>,
                            val leaderIn: List<GetCommunitiesItem>,
                            val userId: CyberName,
                            val registration: UserRegistration) {

    @JsonClass(generateAdapter = true)
    data class Subscribers(val usersCount: Int,
                           val communitiesCount: Int)

    @JsonClass(generateAdapter = true)
    data class UserRegistration(val time: Date)

    @JsonClass(generateAdapter = true)
    data class UserStats(val postsCount: Long,
                         val reputation: Long,
                         val commentsCount: Long)

    @JsonClass(generateAdapter = true)
    data class UserSubscriptions(
            val usersCount: Int,
            val communitiesCount: Int)
}

