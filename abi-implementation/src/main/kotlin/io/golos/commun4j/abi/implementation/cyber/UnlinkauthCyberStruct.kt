// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class UnlinkauthCyberStruct(
  val account: CyberName,
  val code: CyberName,
  val type: CyberName
) {
  val structName: String = "unlinkauth"

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getCode: CyberName
    @CyberNameCompress
    get() = code

  val getType: CyberName
    @CyberNameCompress
    get() = type

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishUnlinkauthCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
