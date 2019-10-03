package io.golos.commun4j.sharedmodel

data class CheckSum256(val value: ByteArray) {
    init {
        if (value.size != 32) throw IllegalStateException("checksum256 must be 32 bytes wide. now = ${value.size} ")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CheckSum256

        if (!value.contentEquals(other.value)) return false

        return true
    }

    override fun hashCode(): Int {
        return value.contentHashCode()
    }
}