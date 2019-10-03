// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.TimestampCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberTimeStampSeconds
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class StateInfoCyberStruct(
  val id: Long,
  val last_close_bid: CyberTimeStampSeconds
) {
  val structName: String = "state_info"

  val getId: Long
    @LongCompress
    get() = id

  val getLastCloseBid: CyberTimeStampSeconds
    @TimestampCompress
    get() = last_close_bid

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishStateInfoCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
