// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.stake

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.NullableCompress
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
data class OpenArgsCyberStakeStruct(
  val owner: CyberName,
  val token_code: CyberSymbolCode,
  val ram_payer: CyberName?
) {
  val structName: String = "open_args"

  val getOwner: CyberName
    @CyberNameCompress
    get() = owner

  val getTokenCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = token_code

  val getRamPayer: CyberName?
    @CyberNameCompress
    @NullableCompress
    get() = ram_payer

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishOpenArgsCyberStakeStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
