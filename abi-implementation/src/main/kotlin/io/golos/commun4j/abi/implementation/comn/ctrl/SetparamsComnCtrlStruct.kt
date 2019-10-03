// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.ctrl

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.InterfaceCollectionCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class SetparamsComnCtrlStruct(
  val point: CyberSymbolCode,
  val params: List<CtrlParamComnCtrlInterface>
) {
  val structName: String = "setparams"

  val getPoint: CyberSymbolCode
    @SymbolCodeCompress
    get() = point

  val getParams: List<CtrlParamComnCtrlInterface>
    @InterfaceCollectionCompress
    get() = params

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishSetparamsComnCtrlStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
