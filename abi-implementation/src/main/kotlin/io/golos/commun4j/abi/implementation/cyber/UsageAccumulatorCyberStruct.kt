// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.IntCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class UsageAccumulatorCyberStruct(
  val last_ordinal: Int,
  val value_ex: Long,
  val consumed: Long
) {
  val structName: String = "usage_accumulator"

  val getLastOrdinal: Int
    @IntCompress
    get() = last_ordinal

  val getValueEx: Long
    @LongCompress
    get() = value_ex

  val getConsumed: Long
    @LongCompress
    get() = consumed

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishUsageAccumulatorCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
