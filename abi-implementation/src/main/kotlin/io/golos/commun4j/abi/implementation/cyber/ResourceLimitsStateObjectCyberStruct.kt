// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.LongCollectionCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ResourceLimitsStateObjectCyberStruct(
  val id: Long,
  val block_usage_accumulators: List<UsageAccumulatorCyberStruct>,
  val pending_usage: List<Long>,
  val virtual_limits: List<Long>
) {
  val structName: String = "resource_limits_state_object"

  val getId: Long
    @LongCompress
    get() = id

  val getBlockUsageAccumulators: List<UsageAccumulatorCyberStruct>
    @CollectionCompress
    get() = block_usage_accumulators

  val getPendingUsage: List<Long>
    @LongCollectionCompress
    get() = pending_usage

  val getVirtualLimits: List<Long>
    @LongCollectionCompress
    get() = virtual_limits

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishResourceLimitsStateObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
