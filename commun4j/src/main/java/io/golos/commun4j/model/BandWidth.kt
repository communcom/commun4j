package io.golos.commun4j.model

import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.sharedmodel.CyberName

enum class BandWidthSource {
    COMN_SERVICES, USING_KEY
}

data class BandWidthRequest @JvmOverloads constructor(val source: BandWidthSource,
                                                      val actor: CyberName,
                                                      val key: EosPrivateKey? = null) {
    companion object {
        val bandWidthFromGolosRequest = BandWidthRequest(BandWidthSource.COMN_SERVICES, CyberName("comn"))

        fun EosPrivateKey.glsBandWidthRequest() = BandWidthRequest(BandWidthSource.USING_KEY, CyberName("comn"), this)
    }
}