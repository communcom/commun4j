// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BoolCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.PublicKeyCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.TimestampCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.core.crypto.EosPublicKey
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.commun4j.sharedmodel.CyberTimeStampSeconds
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class StakeCandidateObjectCyberStruct(
  val id: Long,
  val token_code: CyberSymbolCode,
  val account: CyberName,
  val latest_pick: CyberTimeStampSeconds,
  val votes: Long,
  val priority: Long,
  val signing_key: EosPublicKey,
  val enabled: Boolean
) {
  val structName: String = "stake_candidate_object"

  val getId: Long
    @LongCompress
    get() = id

  val getTokenCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = token_code

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getLatestPick: CyberTimeStampSeconds
    @TimestampCompress
    get() = latest_pick

  val getVotes: Long
    @LongCompress
    get() = votes

  val getPriority: Long
    @LongCompress
    get() = priority

  val getSigningKey: EosPublicKey
    @PublicKeyCompress
    get() = signing_key

  val getEnabled: Boolean
    @BoolCompress
    get() = enabled

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishStakeCandidateObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
