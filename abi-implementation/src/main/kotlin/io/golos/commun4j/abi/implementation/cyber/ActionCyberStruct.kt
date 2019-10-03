// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BytesCompress
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.ByteArray
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ActionCyberStruct(
  val account: CyberName,
  val name: CyberName,
  val authorization: List<PermissionLevelCyberStruct>,
  val data: ByteArray
) {
  val structName: String = "action"

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getName: CyberName
    @CyberNameCompress
    get() = name

  val getAuthorization: List<PermissionLevelCyberStruct>
    @CollectionCompress
    get() = authorization

  val getData: ByteArray
    @BytesCompress
    get() = data

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishActionCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
