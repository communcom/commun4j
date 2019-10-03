// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.msig

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class OldApprovalsInfoCyberMsigStruct(
  val proposal_name: CyberName,
  val requested_approvals: List<PermissionLevelCyberMsigStruct>,
  val provided_approvals: List<PermissionLevelCyberMsigStruct>
) {
  val structName: String = "old_approvals_info"

  val getProposalName: CyberName
    @CyberNameCompress
    get() = proposal_name

  val getRequestedApprovals: List<PermissionLevelCyberMsigStruct>
    @CollectionCompress
    get() = requested_approvals

  val getProvidedApprovals: List<PermissionLevelCyberMsigStruct>
    @CollectionCompress
    get() = provided_approvals

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishOldApprovalsInfoCyberMsigStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
