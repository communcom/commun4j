// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.stake

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
data class WithdrawArgsCyberStakeStruct(
  val account: CyberName,
  val quantity: CyberAsset
) {
  val structName: String = "withdraw_args"

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getQuantity: CyberAsset
    @AssetCompress
    get() = quantity

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishWithdrawArgsCyberStakeStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
