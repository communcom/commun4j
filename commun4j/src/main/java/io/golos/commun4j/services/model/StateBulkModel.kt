package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberAsset
import io.golos.commun4j.sharedmodel.CyberName

@JsonClass(generateAdapter = true)
data class UserAndPermlinkPair(val userId: CyberName, val permlink: String)

@JsonClass(generateAdapter = true)
internal data class GetStateBulkRequest(val posts: List<UserAndPermlinkPair>?)

@JsonClass(generateAdapter = true)
data class GetStateBulkResponseItem(val topCount: Int,
                                    val collectionEnd: String,
                                    val reward: CyberAsset,
                                    val isClosed: Boolean,
                                    val contentId: UserAndPermlinkPair)


typealias GetStateBulkResponse = Map<String, List<GetStateBulkResponseItem>>