// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.token

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.AssetCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberAsset
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class BalanceEventCyberTokenStruct(
  val account: CyberName,
  val balance: CyberAsset,
  val payments: CyberAsset
) {
  val structName: String = "balance_event"

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getBalance: CyberAsset
    @AssetCompress
    get() = balance

  val getPayments: CyberAsset
    @AssetCompress
    get() = payments

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishBalanceEventCyberTokenStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
