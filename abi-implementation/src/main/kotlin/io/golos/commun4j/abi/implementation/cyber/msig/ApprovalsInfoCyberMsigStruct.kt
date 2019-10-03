// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.msig

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ByteCompress
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Byte
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ApprovalsInfoCyberMsigStruct(
  val version: Byte,
  val proposal_name: CyberName,
  val requested_approvals: List<ApprovalCyberMsigStruct>,
  val provided_approvals: List<ApprovalCyberMsigStruct>
) {
  val structName: String = "approvals_info"

  val getVersion: Byte
    @ByteCompress
    get() = version

  val getProposalName: CyberName
    @CyberNameCompress
    get() = proposal_name

  val getRequestedApprovals: List<ApprovalCyberMsigStruct>
    @CollectionCompress
    get() = requested_approvals

  val getProvidedApprovals: List<ApprovalCyberMsigStruct>
    @CollectionCompress
    get() = provided_approvals

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishApprovalsInfoCyberMsigStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
