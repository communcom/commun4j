// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.emit

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BoolCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class IssuerewardArgsComnEmitStruct(
  val commun_code: CyberSymbolCode,
  val for_leaders: Boolean
) {
  val structName: String = "issuereward_args"

  val getCommunCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = commun_code

  val getForLeaders: Boolean
    @BoolCompress
    get() = for_leaders

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishIssuerewardArgsComnEmitStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
