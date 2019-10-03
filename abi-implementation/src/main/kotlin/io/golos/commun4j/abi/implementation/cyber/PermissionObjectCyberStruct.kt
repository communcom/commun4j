// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberTimeStampMicroseconds
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class PermissionObjectCyberStruct(
  val id: Long,
  val usage_id: Long,
  val parent: Long,
  val owner: CyberName,
  val name: CyberName,
  val last_updated: CyberTimeStampMicroseconds,
  val auth: SharedAuthorityCyberStruct
) {
  val structName: String = "permission_object"

  val getId: Long
    @LongCompress
    get() = id

  val getUsageId: Long
    @LongCompress
    get() = usage_id

  val getParent: Long
    @LongCompress
    get() = parent

  val getOwner: CyberName
    @CyberNameCompress
    get() = owner

  val getName: CyberName
    @CyberNameCompress
    get() = name

  val getLastUpdated: CyberTimeStampMicroseconds
    @LongCompress
    get() = last_updated

  val getAuth: SharedAuthorityCyberStruct
    @ChildCompress
    get() = auth

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishPermissionObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
