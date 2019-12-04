package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

@JsonClass(generateAdapter = true)
internal class SubscribersRequest(val userId: CyberName?,
                                  val communityId: String?,
                                  val limit: Int?,
                                  val offset: Int?)

//{
//    "userId": "tst3fwejlkvx",
//    "username": "heidenreich-odessa-v",
//    "avatarUrl": "https://i.pravatar.cc/300?u=e8f13335de09997afb44b808a2fdada046016d92",
//    "subscribersCount": 1,
//    "postsCount": 3
//}
@JsonClass(generateAdapter = true)
data class SubscriberItem(val userId: CyberName, val username: String?, val avatarUrl: String?,
                          val subscribersCount: Int?, val postsCount: Int?, val isSubscribed: Boolean?)

@JsonClass(generateAdapter = true)
data class SubscribedUsersResponse(val items: List<SubscriberItem>) : List<SubscriberItem> by items

