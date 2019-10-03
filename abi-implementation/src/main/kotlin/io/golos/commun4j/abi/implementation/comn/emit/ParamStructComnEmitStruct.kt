// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.emit

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ParamStructComnEmitStruct(
  val id: Long,
  val annual_emission_rate: Short,
  val leaders_reward_prop: Short
) {
  val structName: String = "param_struct"

  val getId: Long
    @LongCompress
    get() = id

  val getAnnualEmissionRate: Short
    @ShortCompress
    get() = annual_emission_rate

  val getLeadersRewardProp: Short
    @ShortCompress
    get() = leaders_reward_prop

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishParamStructComnEmitStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
