package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

//content.getReferralUsers({ limit, offset })
//>> {"jsonrpc":"2.0","id":65,"result":{"items":[{"userId":"tst5lxhajbsq","username":"how-are-you","avatarUrl":"https://img.commun.com/images/2k6yPSrZYCGQwNcEoLifJmebsdAh.png","isSubscribed":false,"subscribersCount":0,"postsCount":0}]}}

@JsonClass(generateAdapter = true)
internal data class GetReferralUsersRequest(val limit: Int?, val offset: Int?)

@JsonClass(generateAdapter = true)
data class GetReferralUsersResponse(val items: List<GetReferralUsersResponseItem>)

@JsonClass(generateAdapter = true)
data class GetReferralUsersResponseItem(val userId: CyberName, val username: String?, val avatarUrl: String?, val isSubscribed: Boolean?, val subscribersCount: Int?, val postsCount: Int?)