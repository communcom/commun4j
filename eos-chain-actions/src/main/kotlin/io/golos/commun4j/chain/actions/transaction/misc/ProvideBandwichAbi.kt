package io.golos.commun4j.chain.actions.transaction.misc

import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.NameCompress
import io.golos.commun4j.sharedmodel.CyberName

@Abi
data class ProvideBandwichAbi(private val provider: CyberName,
                              private val recipient: CyberName) {
    val getProvider: String
        @NameCompress get() = provider.name
    val getRecipient
        @NameCompress get() = recipient.name
}