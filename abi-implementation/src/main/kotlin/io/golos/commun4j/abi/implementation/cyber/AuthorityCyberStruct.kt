// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.IntCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Int
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class AuthorityCyberStruct(
  val threshold: Int,
  val keys: List<KeyWeightCyberStruct>,
  val accounts: List<PermissionLevelWeightCyberStruct>,
  val waits: List<WaitWeightCyberStruct>
) {
  val structName: String = "authority"

  val getThreshold: Int
    @IntCompress
    get() = threshold

  val getKeys: List<KeyWeightCyberStruct>
    @CollectionCompress
    get() = keys

  val getAccounts: List<PermissionLevelWeightCyberStruct>
    @CollectionCompress
    get() = accounts

  val getWaits: List<WaitWeightCyberStruct>
    @CollectionCompress
    get() = waits

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishAuthorityCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
