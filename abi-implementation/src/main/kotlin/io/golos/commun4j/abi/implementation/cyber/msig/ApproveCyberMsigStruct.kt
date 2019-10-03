// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.msig

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CheckSumCompress
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CheckSum256
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ApproveCyberMsigStruct(
  val proposer: CyberName,
  val proposal_name: CyberName,
  val level: PermissionLevelCyberMsigStruct,
  val proposal_hash: CheckSum256
) {
  val structName: String = "approve"

  val getProposer: CyberName
    @CyberNameCompress
    get() = proposer

  val getProposalName: CyberName
    @CyberNameCompress
    get() = proposal_name

  val getLevel: PermissionLevelCyberMsigStruct
    @ChildCompress
    get() = level

  val getProposalHash: CheckSum256
    @CheckSumCompress
    get() = proposal_hash

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishApproveCyberMsigStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
