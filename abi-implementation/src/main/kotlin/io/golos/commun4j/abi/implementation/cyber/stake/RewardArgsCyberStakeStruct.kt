// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.stake

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.SymbolCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberSymbol
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class RewardArgsCyberStakeStruct(
  val rewards: List<NameIntPairCyberStakeStruct>,
  val sym: CyberSymbol
) {
  val structName: String = "reward_args"

  val getRewards: List<NameIntPairCyberStakeStruct>
    @CollectionCompress
    get() = rewards

  val getSym: CyberSymbol
    @SymbolCompress
    get() = sym

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishRewardArgsCyberStakeStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
