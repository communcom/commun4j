// class is generated, and would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.stake

import io.golos.commun4j.abi.implementation.BandWidthProvideOption
import io.golos.commun4j.abi.implementation.createBandwidthActionAbi
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.AbiBinaryGenTransactionWriter
import io.golos.commun4j.chain.actions.transaction.TransactionPusher
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.sharedmodel.Commun4jConfig
import kotlin.String
import kotlin.collections.List
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

class UpdatefundsCyberStakeAction(
  val struct: UpdatefundsArgsCyberStakeStruct
) {
  @JvmOverloads
  fun toActionAbi(
    transactionAuth: List<TransactionAuthorizationAbi>,
    contractName: String = Companion.contractName,
    actionName: String = Companion.actionName
  ) =  ActionAbi(contractName, actionName,
              transactionAuth, struct.toHex())
  @JvmOverloads
  fun createSignedTransactionForProvideBw(
    transactionAuth: List<TransactionAuthorizationAbi>,
    key: EosPrivateKey,
    withConfig: Commun4jConfig,
    provideBandwidth: BandWidthProvideOption,
    contractName: String = Companion.contractName,
    actionName: String = Companion.actionName
  ) = TransactionPusher.createSignedTransaction(
              listOf(toActionAbi(transactionAuth, contractName, actionName),
                      createBandwidthActionAbi(transactionAuth[0].actor,
      provideBandwidth.provider)),
              listOf(key),
              withConfig.blockChainHttpApiUrl,
              withConfig.logLevel,
              withConfig.httpLogger)
  @JvmOverloads
  fun push(
    transactionAuth: List<TransactionAuthorizationAbi>,
    key: EosPrivateKey,
    withConfig: Commun4jConfig,
    provideBandwidth: BandWidthProvideOption? = null,
    bandwidthProviderKey: EosPrivateKey? = null,
    contractName: String = Companion.contractName,
    actionName: String = Companion.actionName
  ) = TransactionPusher.pushTransaction(arrayListOf(toActionAbi(transactionAuth,
              contractName, actionName)).apply { if (provideBandwidth != null)
      this.add(createBandwidthActionAbi(transactionAuth[0].actor, provideBandwidth.provider)) },
              key, struct::class.java,
              withConfig.blockChainHttpApiUrl, provideBandwidth != null, bandwidthProviderKey,
              withConfig.logLevel,
              withConfig.httpLogger)
  companion object {
    @JvmStatic
    val actionName: String = "updatefunds"

    @JvmStatic
    val contractName: String = "cyber.stake"
  }
}
