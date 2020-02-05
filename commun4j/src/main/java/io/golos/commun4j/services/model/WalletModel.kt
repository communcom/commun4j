package io.golos.commun4j.services.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberAsset
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import java.util.*

enum class TransactionDirection {
    IN, OUT, ALL;

    override fun toString(): String {
        return when (this) {
            IN -> "in"
            OUT -> "out"
            ALL -> "all"
        }
    }
}

@JsonClass(generateAdapter = true)
data class GetTokensInfoResponse(val items: List<TokenItem>) : List<TokenItem> by items

@JsonClass(generateAdapter = true)
data class TokenItem(val issuer: CyberName, @Json(name = "max_supply") val maxSupply: CyberAsset,
                     val supply: CyberAsset, val sym: CyberSymbolCode)

@JsonClass(generateAdapter = true)
data class GetClaimHistoryRequest(val userId: CyberName, val sequenceKey: String?, val tokens: List<String>?, val limit: Int?)

@JsonClass(generateAdapter = true)
data class GetClaimHistoryResult(val claims: List<ClaimHistoryItem>)

@JsonClass(generateAdapter = true)
data class ClaimHistoryItem(val id: String, val userId: CyberName, val blockNum: Int, val trxId: String,
                            val timestamp: Date, val sym: CyberSymbolCode, val quantity: Double)
