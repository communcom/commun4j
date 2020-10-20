package io.golos.commun4j.services.model
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DonationPostModel(val userId: String, val permlink: String)

@JsonClass(generateAdapter = true)
data class DonationContentModel(val userId: String, val permlink: String, val communityId: String)

@JsonClass(generateAdapter = true)
data class DonationSenderModel(val username: String?, val avatarUrl: String?, val userId: String?)

@JsonClass(generateAdapter = true)
data class DonationModel(val quantity: String, val sender: DonationSenderModel?)

@JsonClass(generateAdapter = true)
data class DonationItem(val contentId: DonationContentModel?, val donations: List<DonationModel>?, val totalAmount: Double?)

@JsonClass(generateAdapter = true)
data class GetDonationResponse(val items:List<DonationItem>) : List<DonationItem> by items

@JsonClass(generateAdapter = true)
internal data class GetDonationRequest(val posts: List<DonationPostModel>)