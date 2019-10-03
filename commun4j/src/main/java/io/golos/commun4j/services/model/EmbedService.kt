package io.golos.commun4j.services.model

internal enum class EmbedService {
    OEMBED, IFRAMELY;

    override fun toString(): String {
        return when(this){
            OEMBED -> "oembed"
            IFRAMELY -> "iframely"
        }
    }
}