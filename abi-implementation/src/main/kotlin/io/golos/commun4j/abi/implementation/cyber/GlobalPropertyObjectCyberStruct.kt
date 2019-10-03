// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.IntCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.NullableCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class GlobalPropertyObjectCyberStruct(
  val id: Long,
  val proposed_schedule_block_num: Int?,
  val proposed_schedule: ProducerScheduleCyberStruct,
  val configuration: ChainConfigCyberStruct
) {
  val structName: String = "global_property_object"

  val getId: Long
    @LongCompress
    get() = id

  val getProposedScheduleBlockNum: Int?
    @IntCompress
    @NullableCompress
    get() = proposed_schedule_block_num

  val getProposedSchedule: ProducerScheduleCyberStruct
    @ChildCompress
    get() = proposed_schedule

  val getConfiguration: ChainConfigCyberStruct
    @ChildCompress
    get() = configuration

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishGlobalPropertyObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
