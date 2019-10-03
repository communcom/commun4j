// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.StringCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class DomainObjectCyberStruct(
  val id: Long,
  val owner: CyberName,
  val linked_to: CyberName,
  val creation_date: String,
  val name: String
) {
  val structName: String = "domain_object"

  val getId: Long
    @LongCompress
    get() = id

  val getOwner: CyberName
    @CyberNameCompress
    get() = owner

  val getLinkedTo: CyberName
    @CyberNameCompress
    get() = linked_to

  val getCreationDate: String
    @StringCompress
    get() = creation_date

  val getName: String
    @StringCompress
    get() = name

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishDomainObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
