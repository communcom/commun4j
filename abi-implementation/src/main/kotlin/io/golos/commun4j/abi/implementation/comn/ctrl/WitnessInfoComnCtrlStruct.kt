// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.ctrl

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BoolCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.StringCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class WitnessInfoComnCtrlStruct(
  val name: CyberName,
  val url: String,
  val active: Boolean,
  val total_weight: Long,
  val counter_votes: Long
) {
  val structName: String = "witness_info"

  val getName: CyberName
    @CyberNameCompress
    get() = name

  val getUrl: String
    @StringCompress
    get() = url

  val getActive: Boolean
    @BoolCompress
    get() = active

  val getTotalWeight: Long
    @LongCompress
    get() = total_weight

  val getCounterVotes: Long
    @LongCompress
    get() = counter_votes

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishWitnessInfoComnCtrlStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
