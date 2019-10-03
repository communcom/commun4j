// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.AssetCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberAsset
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class BidnameCyberStruct(
  val bidder: CyberName,
  val newname: CyberName,
  val bid: CyberAsset
) {
  val structName: String = "bidname"

  val getBidder: CyberName
    @CyberNameCompress
    get() = bidder

  val getNewname: CyberName
    @CyberNameCompress
    get() = newname

  val getBid: CyberAsset
    @AssetCompress
    get() = bid

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishBidnameCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
