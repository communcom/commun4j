package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

@JsonClass(generateAdapter = true)
internal data class LeadersRequest(val communityId: String, val limit: Int?, val offset: Int?, val query: String?)

@JsonClass(generateAdapter = true)
data class LeadersResponse(val items: List<LeaderItem>): List<LeaderItem> by items
//
//url": "xsQn8gK12,_x.CVd",
//"rating": "18",
//"isActive": true,
//"userId": "1khdwuolcvl2",
//"position": 1,
//"isVoted": false,
//"ratingPercent": 0.3333333333333333,
//"isSubscribed": false,
//"username": null,
//"avatarUrl": null
@JsonClass(generateAdapter = true)
data class LeaderItem(val url: String,
                      val rating: Double?,
                      val isActive: Boolean?,
                      val username: String?,
                      val userId: CyberName,
                      val position: Int?,
                      val isVoted: Boolean?,
                      val ratingPercent: Double?,
                      val isSubscribed: Boolean?,
                      val userName: String?,
                      val votesCount: Int?,
                      val avatarUrl: String?,
                      val inTop:Boolean)