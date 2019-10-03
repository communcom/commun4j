// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BytesCompress
import io.golos.commun4j.abi.writer.CheckSumCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.StringCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CheckSum256
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberTimeStampMicroseconds
import java.math.BigInteger
import kotlin.ByteArray
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class GeneratedTransactionObjectCyberStruct(
  val id: Long,
  val trx_id: CheckSum256,
  val sender: CyberName,
  val sender_id: BigInteger,
  val delay_until: CyberTimeStampMicroseconds,
  val expiration: CyberTimeStampMicroseconds,
  val published: CyberTimeStampMicroseconds,
  val packed_trx: String
) {
  val structName: String = "generated_transaction_object"

  val getId: Long
    @LongCompress
    get() = id

  val getTrxId: CheckSum256
    @CheckSumCompress
    get() = trx_id

  val getSender: CyberName
    @CyberNameCompress
    get() = sender

  val getSenderId: ByteArray
    @BytesCompress
    get() = ByteArray(16) { 0 }.also { System.arraycopy(sender_id.toByteArray(), 0, it, 0,
        sender_id.toByteArray().size) }.reversedArray()

  val getDelayUntil: CyberTimeStampMicroseconds
    @LongCompress
    get() = delay_until

  val getExpiration: CyberTimeStampMicroseconds
    @LongCompress
    get() = expiration

  val getPublished: CyberTimeStampMicroseconds
    @LongCompress
    get() = published

  val getPackedTrx: String
    @StringCompress
    get() = packed_trx

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishGeneratedTransactionObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
