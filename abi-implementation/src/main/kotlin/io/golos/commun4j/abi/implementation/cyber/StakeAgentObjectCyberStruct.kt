// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ByteCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.TimestampCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.commun4j.sharedmodel.CyberTimeStampSeconds
import kotlin.Byte
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class StakeAgentObjectCyberStruct(
  val id: Long,
  val token_code: CyberSymbolCode,
  val account: CyberName,
  val proxy_level: Byte,
  val last_proxied_update: CyberTimeStampSeconds,
  val balance: Long,
  val proxied: Long,
  val shares_sum: Long,
  val own_share: Long,
  val fee: Short,
  val min_own_staked: Long,
  val provided: Long,
  val received: Long
) {
  val structName: String = "stake_agent_object"

  val getId: Long
    @LongCompress
    get() = id

  val getTokenCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = token_code

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getProxyLevel: Byte
    @ByteCompress
    get() = proxy_level

  val getLastProxiedUpdate: CyberTimeStampSeconds
    @TimestampCompress
    get() = last_proxied_update

  val getBalance: Long
    @LongCompress
    get() = balance

  val getProxied: Long
    @LongCompress
    get() = proxied

  val getSharesSum: Long
    @LongCompress
    get() = shares_sum

  val getOwnShare: Long
    @LongCompress
    get() = own_share

  val getFee: Short
    @ShortCompress
    get() = fee

  val getMinOwnStaked: Long
    @LongCompress
    get() = min_own_staked

  val getProvided: Long
    @LongCompress
    get() = provided

  val getReceived: Long
    @LongCompress
    get() = received

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishStakeAgentObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
