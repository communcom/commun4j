package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName


enum class SubscriptionType {
    USER, COMMUNITY;

    override fun toString(): String {
        return when (this) {
            USER -> "user"
            COMMUNITY -> "community"
        }
    }
}

@JsonClass(generateAdapter = true)
internal class SubscriptionsRequest(val userId: CyberName,
                                    val limit: Int,
                                    val type: String,
                                    val sequenceKey: String?,
                                    val app: String)