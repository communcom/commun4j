// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class AccountSequenceObjectCyberStruct(
  val name: CyberName,
  val recv_sequence: Long,
  val auth_sequence: Long,
  val code_sequence: Long,
  val abi_sequence: Long
) {
  val structName: String = "account_sequence_object"

  val getName: CyberName
    @CyberNameCompress
    get() = name

  val getRecvSequence: Long
    @LongCompress
    get() = recv_sequence

  val getAuthSequence: Long
    @LongCompress
    get() = auth_sequence

  val getCodeSequence: Long
    @LongCompress
    get() = code_sequence

  val getAbiSequence: Long
    @LongCompress
    get() = abi_sequence

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishAccountSequenceObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
