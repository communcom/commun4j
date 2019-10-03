package io.golos.commun4j.sharedmodel

data class CyberAsset(val amount: String) {

    companion object {
        val assetRegexp = "[0-9]+\\.(([0-9]{3})|([0-9]{6}))\\s?[a-zA-Z0-9]{1,7}".toRegex()

        fun threeDigitsPrecision(value: Double) = String.format("%.3f", value)

        fun sixDigitsPrecision(value: Double) = String.format("%.6f", value)


    }
}