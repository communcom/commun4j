// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.ctrl

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class VotewitnessComnCtrlStruct(
  val point: CyberSymbolCode,
  val voter: CyberName,
  val witness: CyberName
) {
  val structName: String = "votewitness"

  val getPoint: CyberSymbolCode
    @SymbolCodeCompress
    get() = point

  val getVoter: CyberName
    @CyberNameCompress
    get() = voter

  val getWitness: CyberName
    @CyberNameCompress
    get() = witness

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishVotewitnessComnCtrlStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
