package io.golos.commun4j.services.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import java.util.*

@JsonClass(generateAdapter = true)
internal data class GetTransferHistoryRequest(val userId: CyberName, val direction: TransferHistoryDirection?, val transferType: TransferHistoryTransferType?,
                                              val symbol: CyberSymbolCode?, val rewards: String?, val limit: Int?, val offset: Int?, val donation: TransferHistoryDonation?, val holdType: TransferHistoryHoldType?)

enum class TransferHistoryDirection {
    @Json(name = "all")
    ALL,
    @Json(name = "send")
    SEND,
    @Json(name = "receive")
    RECEIVE;
}

enum class TransferHistoryTransferType {
    @Json(name = "all")
    ALL,
    @Json(name = "transfer")
    TRANSFER,
    @Json(name = "convert")
    CONVERT,
    @Json(name = "token")
    TOKEN,
    @Json(name = "point")
    POINT,
    @Json(name = "none")
    NONE;
}

enum class TransferHistoryDonation {
    @Json(name = "all")
    ALL,
    @Json(name = "none")
    NONE;
}

enum class TransferHistoryHoldType {
    @Json(name = "like")
    LIKE,
    @Json(name = "dislike")
    DISLIKE,
    @Json(name = "none")
    NONE;
}

@JsonClass(generateAdapter = true)
data class GetTransferHistoryResponse(val items: List<GetTransferHistoryResponseItem>) : List<GetTransferHistoryResponseItem> by items

@JsonClass(generateAdapter = true)
data class GetTransferHistoryResponseItem(val id: String, val sender: TransferHistorySender, val receiver: TransferHistoryReceiver,
                                          val quantity: Double, val symbol: CyberSymbolCode, val point: TransferHistoryPoint?,
                                          val trxId: String, val memo: String?, val timestamp: Date, val meta: TransferHistoryMeta)

@JsonClass(generateAdapter = true)
data class TransferHistorySender(val userId: CyberName, val username: String?, val avatarUrl: String?)

@JsonClass(generateAdapter = true)
data class TransferHistoryReceiver(val userId: CyberName, val avatarUrl: String?, val username: String?)

@JsonClass(generateAdapter = true)
data class TransferHistoryPoint(val name: String?, val logo: String?, val symbol: CyberSymbolCode?)

@JsonClass(generateAdapter = true)
data class TransferHistoryMeta(val actionType: String, val transferType: TransferHistoryTransferType?, val exchangeAmount: Double?, val holdType: String?, val direction: TransferHistoryDirection)
