package io.golos.commun4j.model

import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.sharedmodel.CyberName

enum class BandWidthSource {
    GOLOSIO_SERVICES, USING_KEY
}

data class BandWidthRequest @JvmOverloads constructor(val source: BandWidthSource,
                                                      val actor: CyberName,
                                                      val key: EosPrivateKey? = null) {
    companion object {
        val bandWidthFromGolosRequest = BandWidthRequest(BandWidthSource.GOLOSIO_SERVICES, CyberName("gls"))

        fun EosPrivateKey.glsBandWidthRequest() = BandWidthRequest(BandWidthSource.USING_KEY, CyberName("gls"), this)
    }
}