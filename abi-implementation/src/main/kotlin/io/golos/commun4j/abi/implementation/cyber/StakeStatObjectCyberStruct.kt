// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BoolCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.TimestampCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.commun4j.sharedmodel.CyberTimeStampSeconds
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class StakeStatObjectCyberStruct(
  val id: Long,
  val token_code: CyberSymbolCode,
  val total_staked: Long,
  val total_votes: Long,
  val last_reward: CyberTimeStampSeconds,
  val enabled: Boolean
) {
  val structName: String = "stake_stat_object"

  val getId: Long
    @LongCompress
    get() = id

  val getTokenCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = token_code

  val getTotalStaked: Long
    @LongCompress
    get() = total_staked

  val getTotalVotes: Long
    @LongCompress
    get() = total_votes

  val getLastReward: CyberTimeStampSeconds
    @TimestampCompress
    get() = last_reward

  val getEnabled: Boolean
    @BoolCompress
    get() = enabled

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishStakeStatObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
