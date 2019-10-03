// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.msig

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberTimeStampMicroseconds
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class InvalidationCyberMsigStruct(
  val account: CyberName,
  val last_invalidation_time: CyberTimeStampMicroseconds
) {
  val structName: String = "invalidation"

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getLastInvalidationTime: CyberTimeStampMicroseconds
    @LongCompress
    get() = last_invalidation_time

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishInvalidationCyberMsigStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
