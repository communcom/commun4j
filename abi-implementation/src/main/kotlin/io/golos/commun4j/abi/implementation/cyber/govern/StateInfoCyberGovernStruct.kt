// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.govern

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.IntCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.TimestampCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberTimeStampSeconds
import kotlin.Int
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class StateInfoCyberGovernStruct(
  val id: Long,
  val last_schedule_increase: CyberTimeStampSeconds,
  val block_num: Int,
  val target_emission_per_block: Long,
  val funds: Long,
  val last_propose_block_num: Int,
  val required_producers_num: Short,
  val last_producers_num: Short
) {
  val structName: String = "state_info"

  val getId: Long
    @LongCompress
    get() = id

  val getLastScheduleIncrease: CyberTimeStampSeconds
    @TimestampCompress
    get() = last_schedule_increase

  val getBlockNum: Int
    @IntCompress
    get() = block_num

  val getTargetEmissionPerBlock: Long
    @LongCompress
    get() = target_emission_per_block

  val getFunds: Long
    @LongCompress
    get() = funds

  val getLastProposeBlockNum: Int
    @IntCompress
    get() = last_propose_block_num

  val getRequiredProducersNum: Short
    @ShortCompress
    get() = required_producers_num

  val getLastProducersNum: Short
    @ShortCompress
    get() = last_producers_num

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishStateInfoCyberGovernStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
