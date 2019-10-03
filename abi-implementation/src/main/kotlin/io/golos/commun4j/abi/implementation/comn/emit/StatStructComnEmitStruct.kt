// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.emit

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberTimeStampMicroseconds
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class StatStructComnEmitStruct(
  val id: Long,
  val latest_mosaics_reward: CyberTimeStampMicroseconds,
  val latest_leaders_reward: CyberTimeStampMicroseconds
) {
  val structName: String = "stat_struct"

  val getId: Long
    @LongCompress
    get() = id

  val getLatestMosaicsReward: CyberTimeStampMicroseconds
    @LongCompress
    get() = latest_mosaics_reward

  val getLatestLeadersReward: CyberTimeStampMicroseconds
    @LongCompress
    get() = latest_leaders_reward

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishStatStructComnEmitStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
