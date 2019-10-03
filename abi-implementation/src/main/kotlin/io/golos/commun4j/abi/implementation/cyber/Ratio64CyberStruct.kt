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
data class Ratio64CyberStruct(
  val numerator: Long,
  val denominator: Long
) {
  val structName: String = "ratio64"

  val getNumerator: Long
    @LongCompress
    get() = numerator

  val getDenominator: Long
    @LongCompress
    get() = denominator

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishRatio64CyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
