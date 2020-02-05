package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode

@JsonClass(generateAdapter = true)
internal data class GetUserBalanceRequest(val userId: String)

@JsonClass(generateAdapter = true)
data class GetUserBalanceResponse(val userId: CyberName, val balances: List<GetUserBalanceItem>): List<GetUserBalanceItem> by balances

@JsonClass(generateAdapter = true)
data class GetUserBalanceItem(val symbol: CyberSymbolCode, val balance: Double, val logo: String?,
                              val name: String?, val frozen: Double?, val price: Double?, val transferFee: Int?)