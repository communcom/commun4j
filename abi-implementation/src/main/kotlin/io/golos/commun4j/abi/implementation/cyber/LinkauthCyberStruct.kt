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
data class LinkauthCyberStruct(
  val account: CyberName,
  val code: CyberName,
  val type: CyberName,
  val requirement: CyberName
) {
  val structName: String = "linkauth"

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getCode: CyberName
    @CyberNameCompress
    get() = code

  val getType: CyberName
    @CyberNameCompress
    get() = type

  val getRequirement: CyberName
    @CyberNameCompress
    get() = requirement

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishLinkauthCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
