package io.golos.commun4j.services.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import io.golos.commun4j.sharedmodel.CyberSymbolCode

@JsonClass(generateAdapter = true)
internal data class GetWalletBuyPriceRequest(val pointSymbol: CyberSymbolCode, val quantity: WalletQuantity)

@JsonClass(generateAdapter = true)
data class GetWalletBuyPriceResponse(val price: WalletQuantity)

@JsonClass(generateAdapter = true)
data class WalletQuantity(val stringRepresentation: String) {
    val quantity: Double = stringRepresentation.split(" ").first().toDouble()
    val symbolCode: CyberSymbolCode = CyberSymbolCode(stringRepresentation.split(" ")[1])
}

internal class WalletQuantityAdapter : JsonAdapter<WalletQuantity>() {
    override fun fromJson(reader: JsonReader): WalletQuantity? {
        val value = reader.nextString()
        return value?.let { WalletQuantity(it) }
    }

    override fun toJson(writer: JsonWriter, value: WalletQuantity?) {
        writer.value(value?.stringRepresentation ?: "")
    }
}

@JsonClass(generateAdapter = true)
internal data class GetWalletSellPriceRequest(val quantity: WalletQuantity)

@JsonClass(generateAdapter = true)
data class GetWalletSellPriceResponse(val price: WalletQuantity?)