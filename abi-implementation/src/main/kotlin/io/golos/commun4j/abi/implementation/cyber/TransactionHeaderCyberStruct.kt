// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ByteCompress
import io.golos.commun4j.abi.writer.IntCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.TimestampCompress
import io.golos.commun4j.abi.writer.VariableUIntCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberTimeStampSeconds
import io.golos.commun4j.sharedmodel.Varuint
import kotlin.Byte
import kotlin.Int
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class TransactionHeaderCyberStruct(
  val expiration: CyberTimeStampSeconds,
  val ref_block_num: Short,
  val ref_block_prefix: Int,
  val max_net_usage_words: Varuint,
  val max_cpu_usage_ms: Byte,
  val max_ram_kbytes: Varuint,
  val max_storage_kbytes: Varuint,
  val delay_sec: Varuint
) {
  val structName: String = "transaction_header"

  val getExpiration: CyberTimeStampSeconds
    @TimestampCompress
    get() = expiration

  val getRefBlockNum: Short
    @ShortCompress
    get() = ref_block_num

  val getRefBlockPrefix: Int
    @IntCompress
    get() = ref_block_prefix

  val getMaxNetUsageWords: Varuint
    @VariableUIntCompress
    get() = max_net_usage_words

  val getMaxCpuUsageMs: Byte
    @ByteCompress
    get() = max_cpu_usage_ms

  val getMaxRamKbytes: Varuint
    @VariableUIntCompress
    get() = max_ram_kbytes

  val getMaxStorageKbytes: Varuint
    @VariableUIntCompress
    get() = max_storage_kbytes

  val getDelaySec: Varuint
    @VariableUIntCompress
    get() = delay_sec

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishTransactionHeaderCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
