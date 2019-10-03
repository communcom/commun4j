// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BytesCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import java.math.BigInteger
import kotlin.ByteArray
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class OnerrorCyberStruct(
  val sender_id: BigInteger,
  val sent_trx: ByteArray
) {
  val structName: String = "onerror"

  val getSenderId: ByteArray
    @BytesCompress
    get() = ByteArray(16) { 0 }.also { System.arraycopy(sender_id.toByteArray(), 0, it, 0,
        sender_id.toByteArray().size) }.reversedArray()

  val getSentTrx: ByteArray
    @BytesCompress
    get() = sent_trx

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishOnerrorCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
