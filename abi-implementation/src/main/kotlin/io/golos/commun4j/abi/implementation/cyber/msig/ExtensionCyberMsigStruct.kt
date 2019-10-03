// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.msig

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BytesCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.ByteArray
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ExtensionCyberMsigStruct(
  val type: Short,
  val data: ByteArray
) {
  val structName: String = "extension"

  val getType: Short
    @ShortCompress
    get() = type

  val getData: ByteArray
    @BytesCompress
    get() = data

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishExtensionCyberMsigStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
