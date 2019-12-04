package io.golos.commun4j.services.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberAsset
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import java.util.*

enum class CyberTokenType {
    LIQUID, ALL;

    override fun toString(): String {
        return when (this) {
            LIQUID -> "liquid"
            ALL -> "all"
        }
    }
}

@JsonClass(generateAdapter = true)
data class UserBalance(val userId: CyberName, val balances: List<Balance>)

@JsonClass(generateAdapter = true)
data class Balance(val symbol: CyberSymbolCode,
                   val balance: Double,
                   val decs: Int?,
                   val issuer: CyberName?,
                   val logo: String?)

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
data class GetTransferHistoryRequest(val userId: String,
                                     val direction: String?,
                                     val sequenceKey: String?,
                                     val limit: Int?)

@JsonClass(generateAdapter = true)
data class GetTransferHistoryResponse(val items: List<TransferHistoryItem>): List<TransferHistoryItem> by items

@JsonClass(generateAdapter = true)
data class TransferHistoryItem(val id: String, val sender: Sender, val receiver: Receiver,
                               val quantity: Double, val sym: CyberSymbolCode, val trxId: String,
                               val memo: String?, val blockNum: Int, val timestamp: Date, val isIrreversible: Boolean?) {
    @JsonClass(generateAdapter = true)
    data class Sender(val userId: CyberName, val username: String?, val name: String?)

    data class Receiver(val userId: CyberName, val username: String?, val name: String?)
}

@JsonClass(generateAdapter = true)
data class GetTokensInfoResponse(val items: List<TokenItem>): List<TokenItem> by items

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
