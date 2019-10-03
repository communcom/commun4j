// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ByteCompress
import io.golos.commun4j.abi.writer.BytesCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Byte
import kotlin.ByteArray
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class SetcodeCyberStruct(
  val account: CyberName,
  val vmtype: Byte,
  val vmversion: Byte,
  val code: ByteArray
) {
  val structName: String = "setcode"

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getVmtype: Byte
    @ByteCompress
    get() = vmtype

  val getVmversion: Byte
    @ByteCompress
    get() = vmversion

  val getCode: ByteArray
    @BytesCompress
    get() = code

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishSetcodeCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
