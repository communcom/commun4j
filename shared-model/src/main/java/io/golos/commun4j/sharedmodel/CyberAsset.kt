package io.golos.commun4j.sharedmodel

import java.math.BigDecimal

data class CyberAsset(val amount: String) {

    @Transient
    val code = CyberSymbolCode(this.amount.split(" ")[1])

    @Transient
    val quantity = BigDecimal(this.amount.split(" ")[0])

    operator fun plus(other: CyberAsset): CyberAsset {
        ensureCode(other)
        return CyberAsset("${this.quantity + other.quantity} $code")
    }

    operator fun minus(other: CyberAsset): CyberAsset {
        ensureCode(other)
        return CyberAsset("${this.quantity - other.quantity} $code")
    }

    operator fun times(times: Int): CyberAsset {
        return CyberAsset("${this.quantity * BigDecimal(times)} $code")
    }

    private fun ensureCode(other: CyberAsset) {
        if (!this.code.symbolCode.contentEquals(other.code.symbolCode)) throw IllegalArgumentException("cannot make operation on different asset types")
    }

    companion object {
        val assetRegexp = "[0-9]+\\.(([0-9]{3})|([0-9]{6}))\\s?[a-zA-Z0-9]{1,7}".toRegex()

        fun threeDigitsPrecision(value: Double) = String.format("%.3f", value)

        fun sixDigitsPrecision(value: Double) = String.format("%.6f", value)


    }
}