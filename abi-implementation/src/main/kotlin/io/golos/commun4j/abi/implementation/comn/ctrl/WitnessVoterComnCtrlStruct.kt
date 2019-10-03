// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.ctrl

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCollectionCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class WitnessVoterComnCtrlStruct(
  val voter: CyberName,
  val witnesses: List<CyberName>
) {
  val structName: String = "witness_voter"

  val getVoter: CyberName
    @CyberNameCompress
    get() = voter

  val getWitnesses: List<CyberName>
    @CyberNameCollectionCompress
    get() = witnesses

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishWitnessVoterComnCtrlStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
