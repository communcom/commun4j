// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.IntCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Int
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ProducerScheduleCyberStruct(
  val version: Int,
  val producers: List<ProducerKeyCyberStruct>
) {
  val structName: String = "producer_schedule"

  val getVersion: Int
    @IntCompress
    get() = version

  val getProducers: List<ProducerKeyCyberStruct>
    @CollectionCompress
    get() = producers

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishProducerScheduleCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
