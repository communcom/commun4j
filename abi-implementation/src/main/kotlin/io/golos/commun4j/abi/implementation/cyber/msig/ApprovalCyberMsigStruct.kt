// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.msig

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberTimeStampMicroseconds
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ApprovalCyberMsigStruct(
  val level: PermissionLevelCyberMsigStruct,
  val time: CyberTimeStampMicroseconds
) {
  val structName: String = "approval"

  val getLevel: PermissionLevelCyberMsigStruct
    @ChildCompress
    get() = level

  val getTime: CyberTimeStampMicroseconds
    @LongCompress
    get() = time

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishApprovalCyberMsigStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
