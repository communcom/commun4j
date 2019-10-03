package io.golos.commun4j.sharedmodel

class CyberSymbol(val symbol: String) {

    constructor(precision: Byte, name: String) : this("$precision,$name")

    @Transient
    val symbolCode: ByteArray = ByteArray(8)

    init {
        val splited = symbol.split(",")
        symbolCode[0] = splited[0].toByte()
        (0 until 7).forEach {
            symbolCode[it + 1] = splited[1].getOrElse(it) { 0.toChar() }.toByte()
        }
    }
}