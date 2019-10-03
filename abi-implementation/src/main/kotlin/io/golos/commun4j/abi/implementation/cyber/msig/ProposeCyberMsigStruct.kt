// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.msig

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress
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
data class ProposeCyberMsigStruct(
  val proposer: CyberName,
  val proposal_name: CyberName,
  val requested: List<PermissionLevelCyberMsigStruct>,
  val trx: TransactionCyberMsigStruct
) {
  val structName: String = "propose"

  val getProposer: CyberName
    @CyberNameCompress
    get() = proposer

  val getProposalName: CyberName
    @CyberNameCompress
    get() = proposal_name

  val getRequested: List<PermissionLevelCyberMsigStruct>
    @CollectionCompress
    get() = requested

  val getTrx: TransactionCyberMsigStruct
    @ChildCompress
    get() = trx

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishProposeCyberMsigStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
