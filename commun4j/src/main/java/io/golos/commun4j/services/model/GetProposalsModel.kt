package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

@JsonClass(generateAdapter = true)
internal data class GetProposalRequest(val communityIds: List<String>?,
                                       val limit: Int?,
                                       val offset: Int?)

@JsonClass(generateAdapter = true)
data class GetProposalResponse(val items: List<ProposalModel>, val proposalsCount: Int) : List<ProposalModel> by items

@JsonClass(generateAdapter = true)
data class ProposalModel(val proposer: ProposerModel?,
                         val proposalId: String?,
                         val type: String?,
                         val contract: String?,
                         val action: String?,
                         val permission: String?,
                         val data: ProposalDataModel?,
                         val community: CyberCommunity,
                         val isApproved: Boolean,
                         val approvesCount: Int?,
                         val approvesNeed: Int?,
                         val change: ProposalChangeModel?,
                         val expiration: String?,
                         val blockTime: String?,
                         val contentType: String?
)

@JsonClass(generateAdapter = true)
data class ProposerModel(val username: String?, val avatarUrl: String?, val userId: String?)

@JsonClass(generateAdapter = true)
data class ProposalDataModel(val commun_code: String?,
                             val message_id: ProposalMessageIdModel?,
                             val description: String?,
                             val language: String?,
                             val account: ProposalAccountModel?,
                             val rules: String?,
                             val reason: String?,
                             val subject: String?,
                             val avatar_image: String?,
                             val cover_image: String?)

@JsonClass(generateAdapter = true)
data class ProposalChangeModel(val type: String?, val old: String?, val new: String?)

@JsonClass(generateAdapter = true)
data class ProposalMessageIdModel(val type: String?, val old: String?, val new: String?)

@JsonClass(generateAdapter = true)
data class ProposalAccountModel(val userId: CyberName?,
                                val username: String?,
                                val avatarUrl: String?,
                                val subscribersCount: Int?,
                                val postsCount: Int?)