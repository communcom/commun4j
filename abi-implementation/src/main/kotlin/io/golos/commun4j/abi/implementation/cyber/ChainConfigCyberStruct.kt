// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.IntCollectionCompress
import io.golos.commun4j.abi.writer.IntCompress
import io.golos.commun4j.abi.writer.LongCollectionCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.ShortCollectionCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Int
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ChainConfigCyberStruct(
  val base_per_transaction_net_usage: Int,
  val context_free_discount_net_usage_num: Int,
  val context_free_discount_net_usage_den: Int,
  val min_transaction_cpu_usage: Int,
  val min_transaction_ram_usage: Long,
  val max_transaction_lifetime: Int,
  val deferred_trx_expiration_window: Int,
  val max_transaction_delay: Int,
  val max_inline_action_size: Int,
  val max_inline_action_depth: Short,
  val max_authority_depth: Short,
  val ram_size: Long,
  val reserved_ram_size: Long,
  val max_block_usage: List<Long>,
  val max_transaction_usage: List<Long>,
  val target_virtual_limits: List<Long>,
  val min_virtual_limits: List<Long>,
  val max_virtual_limits: List<Long>,
  val usage_windows: List<Int>,
  val virtual_limit_decrease_pct: List<Short>,
  val virtual_limit_increase_pct: List<Short>,
  val account_usage_windows: List<Int>
) {
  val structName: String = "chain_config"

  val getBasePerTransactionNetUsage: Int
    @IntCompress
    get() = base_per_transaction_net_usage

  val getContextFreeDiscountNetUsageNum: Int
    @IntCompress
    get() = context_free_discount_net_usage_num

  val getContextFreeDiscountNetUsageDen: Int
    @IntCompress
    get() = context_free_discount_net_usage_den

  val getMinTransactionCpuUsage: Int
    @IntCompress
    get() = min_transaction_cpu_usage

  val getMinTransactionRamUsage: Long
    @LongCompress
    get() = min_transaction_ram_usage

  val getMaxTransactionLifetime: Int
    @IntCompress
    get() = max_transaction_lifetime

  val getDeferredTrxExpirationWindow: Int
    @IntCompress
    get() = deferred_trx_expiration_window

  val getMaxTransactionDelay: Int
    @IntCompress
    get() = max_transaction_delay

  val getMaxInlineActionSize: Int
    @IntCompress
    get() = max_inline_action_size

  val getMaxInlineActionDepth: Short
    @ShortCompress
    get() = max_inline_action_depth

  val getMaxAuthorityDepth: Short
    @ShortCompress
    get() = max_authority_depth

  val getRamSize: Long
    @LongCompress
    get() = ram_size

  val getReservedRamSize: Long
    @LongCompress
    get() = reserved_ram_size

  val getMaxBlockUsage: List<Long>
    @LongCollectionCompress
    get() = max_block_usage

  val getMaxTransactionUsage: List<Long>
    @LongCollectionCompress
    get() = max_transaction_usage

  val getTargetVirtualLimits: List<Long>
    @LongCollectionCompress
    get() = target_virtual_limits

  val getMinVirtualLimits: List<Long>
    @LongCollectionCompress
    get() = min_virtual_limits

  val getMaxVirtualLimits: List<Long>
    @LongCollectionCompress
    get() = max_virtual_limits

  val getUsageWindows: List<Int>
    @IntCollectionCompress
    get() = usage_windows

  val getVirtualLimitDecreasePct: List<Short>
    @ShortCollectionCompress
    get() = virtual_limit_decrease_pct

  val getVirtualLimitIncreasePct: List<Short>
    @ShortCollectionCompress
    get() = virtual_limit_increase_pct

  val getAccountUsageWindows: List<Int>
    @IntCollectionCompress
    get() = account_usage_windows

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishChainConfigCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
