// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.stake

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class SetminstakedArgsCyberStakeStruct(
  val account: CyberName,
  val token_code: CyberSymbolCode,
  val min_own_staked: Long
) {
  val structName: String = "setminstaked_args"

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getTokenCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = token_code

  val getMinOwnStaked: Long
    @LongCompress
    get() = min_own_staked

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishSetminstakedArgsCyberStakeStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
