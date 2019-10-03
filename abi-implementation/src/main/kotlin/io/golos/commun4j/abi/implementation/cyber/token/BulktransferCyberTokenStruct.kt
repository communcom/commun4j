// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.token

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class BulktransferCyberTokenStruct(
  val from: CyberName,
  val recipients: List<RecipientCyberTokenStruct>
) {
  val structName: String = "bulktransfer"

  val getFrom: CyberName
    @CyberNameCompress
    get() = from

  val getRecipients: List<RecipientCyberTokenStruct>
    @CollectionCompress
    get() = recipients

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishBulktransferCyberTokenStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
