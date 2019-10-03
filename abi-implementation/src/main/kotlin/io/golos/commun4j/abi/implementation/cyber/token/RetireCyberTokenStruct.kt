// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.token

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.AssetCompress
import io.golos.commun4j.abi.writer.StringCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberAsset
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class RetireCyberTokenStruct(
  val quantity: CyberAsset,
  val memo: String
) {
  val structName: String = "retire"

  val getQuantity: CyberAsset
    @AssetCompress
    get() = quantity

  val getMemo: String
    @StringCompress
    get() = memo

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishRetireCyberTokenStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
