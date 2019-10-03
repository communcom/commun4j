// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class DynamicGlobalPropertyObjectCyberStruct(
  val id: Long,
  val global_action_seq: Long
) {
  val structName: String = "dynamic_global_property_object"

  val getId: Long
    @LongCompress
    get() = id

  val getGlobalActionSeq: Long
    @LongCompress
    get() = global_action_seq

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishDynamicGlobalPropertyObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
