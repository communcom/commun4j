// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.ctrl

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BoolCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class WitnessstateComnCtrlStruct(
  val point: CyberSymbolCode,
  val witness: CyberName,
  val weight: Long,
  val active: Boolean
) {
  val structName: String = "witnessstate"

  val getPoint: CyberSymbolCode
    @SymbolCodeCompress
    get() = point

  val getWitness: CyberName
    @CyberNameCompress
    get() = witness

  val getWeight: Long
    @LongCompress
    get() = weight

  val getActive: Boolean
    @BoolCompress
    get() = active

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishWitnessstateComnCtrlStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
