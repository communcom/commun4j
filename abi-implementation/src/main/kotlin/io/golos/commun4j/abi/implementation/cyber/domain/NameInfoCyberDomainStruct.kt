// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.domain

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.StringCollectionCompress
import io.golos.commun4j.abi.writer.StringCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class NameInfoCyberDomainStruct(
  val domain: String,
  val account: CyberName,
  val users: List<String>
) {
  val structName: String = "name_info"

  val getDomain: String
    @StringCompress
    get() = domain

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getUsers: List<String>
    @StringCollectionCompress
    get() = users

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishNameInfoCyberDomainStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
