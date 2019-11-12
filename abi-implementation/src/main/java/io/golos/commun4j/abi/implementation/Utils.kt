package io.golos.commun4j.abi.implementation

import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.AbiBinaryGenTransactionWriter
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.chain.actions.transaction.misc.ProvideBandwichAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.sharedmodel.CyberName

fun createBandwidthActionAbi(forUser: String, actor: CyberName) = ActionAbi("cyber",
        "providebw",
        listOf(TransactionAuthorizationAbi(actor.name, "providebw")),///должно быть `c`
        AbiBinaryGenTransactionWriter(CompressionType.NONE)
                .squishProvideBandwichAbi(
                        ProvideBandwichAbi(
                                CyberName(actor.name),
                                CyberName(forUser))
                        , false).toHex())

data class BandwidthProviding(val from: CyberName, val to: CyberName)

data class BandWidthProvideOption(val providers: List<BandwidthProviding>, val provideBwKeys: List<EosPrivateKey>)