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
import io.golos.commun4j.sharedmodel.CyberTimeStampMicroseconds
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class GemStructComnGalleryStruct(
  val id: Long,
  val mosaic_id: Long,
  val claim_date: CyberTimeStampMicroseconds,
  val points: Long,
  val shares: Long,
  val pledge_points: Long,
  val owner: CyberName,
  val creator: CyberName
) {
  val structName: String = "gem_struct"

  val getId: Long
    @LongCompress
    get() = id

  val getMosaicId: Long
    @LongCompress
    get() = mosaic_id

  val getClaimDate: CyberTimeStampMicroseconds
    @LongCompress
    get() = claim_date

  val getPoints: Long
    @LongCompress
    get() = points

  val getShares: Long
    @LongCompress
    get() = shares

  val getPledgePoints: Long
    @LongCompress
    get() = pledge_points

  val getOwner: CyberName
    @CyberNameCompress
    get() = owner

  val getCreator: CyberName
    @CyberNameCompress
    get() = creator

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishGemStructComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
