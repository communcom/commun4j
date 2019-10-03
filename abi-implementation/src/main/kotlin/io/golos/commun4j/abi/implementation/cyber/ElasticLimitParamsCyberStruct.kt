// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress
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
data class ElasticLimitParamsCyberStruct(
  val target: Long,
  val min: Long,
  val max: Long,
  val periods: Int,
  val decrease_rate: Ratio64CyberStruct,
  val increase_rate: Ratio64CyberStruct
) {
  val structName: String = "elastic_limit_params"

  val getTarget: Long
    @LongCompress
    get() = target

  val getMin: Long
    @LongCompress
    get() = min

  val getMax: Long
    @LongCompress
    get() = max

  val getPeriods: Int
    @IntCompress
    get() = periods

  val getDecreaseRate: Ratio64CyberStruct
    @ChildCompress
    get() = decrease_rate

  val getIncreaseRate: Ratio64CyberStruct
    @ChildCompress
    get() = increase_rate

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishElasticLimitParamsCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
