// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class OpusInfoComnGalleryStruct(
  val name: CyberName,
  val mosaic_pledge: Long,
  val min_mosaic_inclusion: Long,
  val min_gem_inclusion: Long
) {
  val structName: String = "opus_info"

  val getName: CyberName
    @CyberNameCompress
    get() = name

  val getMosaicPledge: Long
    @LongCompress
    get() = mosaic_pledge

  val getMinMosaicInclusion: Long
    @LongCompress
    get() = min_mosaic_inclusion

  val getMinGemInclusion: Long
    @LongCompress
    get() = min_gem_inclusion

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishOpusInfoComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
