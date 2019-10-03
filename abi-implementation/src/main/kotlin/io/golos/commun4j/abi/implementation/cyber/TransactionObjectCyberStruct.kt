// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CheckSumCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.TimestampCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CheckSum256
import io.golos.commun4j.sharedmodel.CyberTimeStampSeconds
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class TransactionObjectCyberStruct(
  val id: Long,
  val expiration: CyberTimeStampSeconds,
  val trx_id: CheckSum256
) {
  val structName: String = "transaction_object"

  val getId: Long
    @LongCompress
    get() = id

  val getExpiration: CyberTimeStampSeconds
    @TimestampCompress
    get() = expiration

  val getTrxId: CheckSum256
    @CheckSumCompress
    get() = trx_id

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishTransactionObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
