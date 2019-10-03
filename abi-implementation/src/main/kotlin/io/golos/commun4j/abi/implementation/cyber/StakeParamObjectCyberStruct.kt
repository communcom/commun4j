// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BytesCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.SymbolCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberSymbol
import kotlin.ByteArray
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class StakeParamObjectCyberStruct(
  val id: Long,
  val token_symbol: CyberSymbol,
  val max_proxies: ByteArray,
  val depriving_window: Long,
  val min_own_staked_for_election: Long
) {
  val structName: String = "stake_param_object"

  val getId: Long
    @LongCompress
    get() = id

  val getTokenSymbol: CyberSymbol
    @SymbolCompress
    get() = token_symbol

  val getMaxProxies: ByteArray
    @BytesCompress
    get() = max_proxies

  val getDeprivingWindow: Long
    @LongCompress
    get() = depriving_window

  val getMinOwnStakedForElection: Long
    @LongCompress
    get() = min_own_staked_for_election

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishStakeParamObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
