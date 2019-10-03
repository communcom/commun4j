// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.emit

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class CreateArgsComnEmitStruct(
  val commun_code: CyberSymbolCode,
  val annual_emission_rate: Short,
  val leaders_reward_prop: Short
) {
  val structName: String = "create_args"

  val getCommunCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = commun_code

  val getAnnualEmissionRate: Short
    @ShortCompress
    get() = annual_emission_rate

  val getLeadersRewardProp: Short
    @ShortCompress
    get() = leaders_reward_prop

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishCreateArgsComnEmitStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
