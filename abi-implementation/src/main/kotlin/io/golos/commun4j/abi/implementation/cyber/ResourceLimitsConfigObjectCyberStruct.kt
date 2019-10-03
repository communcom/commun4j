// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.IntCollectionCompress
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
data class ResourceLimitsConfigObjectCyberStruct(
  val id: Long,
  val limit_parameters: List<ElasticLimitParamsCyberStruct>,
  val account_usage_average_windows: List<Int>
) {
  val structName: String = "resource_limits_config_object"

  val getId: Long
    @LongCompress
    get() = id

  val getLimitParameters: List<ElasticLimitParamsCyberStruct>
    @CollectionCompress
    get() = limit_parameters

  val getAccountUsageAverageWindows: List<Int>
    @IntCollectionCompress
    get() = account_usage_average_windows

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishResourceLimitsConfigObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
