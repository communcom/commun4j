// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.TimestampCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberTimeStampSeconds
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class NameBidCyberStruct(
  val newname: CyberName,
  val high_bidder: CyberName,
  val high_bid: Long,
  val last_bid_time: CyberTimeStampSeconds
) {
  val structName: String = "name_bid"

  val getNewname: CyberName
    @CyberNameCompress
    get() = newname

  val getHighBidder: CyberName
    @CyberNameCompress
    get() = high_bidder

  val getHighBid: Long
    @LongCompress
    get() = high_bid

  val getLastBidTime: CyberTimeStampSeconds
    @TimestampCompress
    get() = last_bid_time

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishNameBidCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
