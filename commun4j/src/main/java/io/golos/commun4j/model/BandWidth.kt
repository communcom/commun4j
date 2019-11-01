package io.golos.commun4j.model

import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.sharedmodel.CyberName

enum class BandWidthSource {
    COMN_SERVICES, USING_KEY
}

data class ClientAuthRequest(val clientName: CyberName, val key: EosPrivateKey) {
    companion object {
        @JvmStatic
        fun createComnAuthRequest(key: EosPrivateKey) = ClientAuthRequest(CyberName("c"), key)
    }
}

data class BandWidthRequest @JvmOverloads constructor(val source: BandWidthSource,
                                                      val actor: CyberName,
                                                      val key: EosPrivateKey? = null) {
    companion object {
        @JvmStatic
        val bandWidthFromComn = BandWidthRequest(BandWidthSource.COMN_SERVICES, CyberName("c"))

        @JvmStatic
        fun bandwidthFromComnUsingItsKey(commProvideBwKey: EosPrivateKey) = BandWidthRequest(BandWidthSource.USING_KEY, CyberName("c"), commProvideBwKey)
    }
}