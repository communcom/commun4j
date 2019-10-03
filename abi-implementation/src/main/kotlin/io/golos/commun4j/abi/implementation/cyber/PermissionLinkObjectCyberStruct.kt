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
data class PermissionLinkObjectCyberStruct(
  val id: Long,
  val account: CyberName,
  val code: CyberName,
  val message_type: CyberName,
  val required_permission: CyberName
) {
  val structName: String = "permission_link_object"

  val getId: Long
    @LongCompress
    get() = id

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getCode: CyberName
    @CyberNameCompress
    get() = code

  val getMessageType: CyberName
    @CyberNameCompress
    get() = message_type

  val getRequiredPermission: CyberName
    @CyberNameCompress
    get() = required_permission

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishPermissionLinkObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
