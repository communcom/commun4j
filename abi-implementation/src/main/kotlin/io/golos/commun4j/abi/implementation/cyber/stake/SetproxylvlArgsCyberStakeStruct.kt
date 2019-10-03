// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.stake

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ByteCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import kotlin.Byte
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class SetproxylvlArgsCyberStakeStruct(
  val account: CyberName,
  val token_code: CyberSymbolCode,
  val level: Byte
) {
  val structName: String = "setproxylvl_args"

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getTokenCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = token_code

  val getLevel: Byte
    @ByteCompress
    get() = level

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishSetproxylvlArgsCyberStakeStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
