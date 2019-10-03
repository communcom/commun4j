// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CheckSumCompress
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.IntCompress
import io.golos.commun4j.abi.writer.NullableCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CheckSum256
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Int
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class BlockHeaderCyberStruct(
  val timestamp: Int,
  val producer: CyberName,
  val confirmed: Short,
  val previous: CheckSum256,
  val transaction_mroot: CheckSum256,
  val action_mroot: CheckSum256,
  val schedule_version: Int,
  val new_producers: ProducerScheduleCyberStruct?,
  val header_extensions: List<ExtensionCyberStruct>
) {
  val structName: String = "block_header"

  val getTimestamp: Int
    @IntCompress
    get() = timestamp

  val getProducer: CyberName
    @CyberNameCompress
    get() = producer

  val getConfirmed: Short
    @ShortCompress
    get() = confirmed

  val getPrevious: CheckSum256
    @CheckSumCompress
    get() = previous

  val getTransactionMroot: CheckSum256
    @CheckSumCompress
    get() = transaction_mroot

  val getActionMroot: CheckSum256
    @CheckSumCompress
    get() = action_mroot

  val getScheduleVersion: Int
    @IntCompress
    get() = schedule_version

  val getNewProducers: ProducerScheduleCyberStruct?
    @ChildCompress
    @NullableCompress
    get() = new_producers

  val getHeaderExtensions: List<ExtensionCyberStruct>
    @CollectionCompress
    get() = header_extensions

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishBlockHeaderCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
