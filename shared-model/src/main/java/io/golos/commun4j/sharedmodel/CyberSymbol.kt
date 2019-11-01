package io.golos.commun4j.sharedmodel


data class CyberSymbolCode(val value: String){
    @Transient
    val symbolCode = ByteArray(8).apply {
        (0..7).forEach {index ->
            this[index] = value.getOrElse(index) {0.toChar()}.toByte()
        }
    }
}