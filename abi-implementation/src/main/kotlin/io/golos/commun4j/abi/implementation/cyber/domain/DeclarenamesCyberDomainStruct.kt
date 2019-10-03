// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.domain

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class DeclarenamesCyberDomainStruct(
  val domains: List<NameInfoCyberDomainStruct>
) {
  val structName: String = "declarenames"

  val getDomains: List<NameInfoCyberDomainStruct>
    @CollectionCompress
    get() = domains

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishDeclarenamesCyberDomainStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
