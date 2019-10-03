// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.point

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.SymbolCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbol
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class CloseArgsComnPointStruct(
  val owner: CyberName,
  val symbol: CyberSymbol
) {
  val structName: String = "close_args"

  val getOwner: CyberName
    @CyberNameCompress
    get() = owner

  val getSymbol: CyberSymbol
    @SymbolCompress
    get() = symbol

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishCloseArgsComnPointStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
