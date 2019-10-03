// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BoolCompress
import io.golos.commun4j.abi.writer.CyberNameCollectionCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberTimeStampMicroseconds
import kotlin.Boolean
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class MosaicStructComnGalleryStruct(
  val id: Long,
  val creator: CyberName,
  val tracery: Long,
  val opus: CyberName,
  val royalty: Short,
  val created: CyberTimeStampMicroseconds,
  val gem_count: Short,
  val points: Long,
  val shares: Long,
  val damn_points: Long,
  val damn_shares: Long,
  val pledge_points: Long,
  val reward: Long,
  val comm_rating: Long,
  val lead_rating: Long,
  val meritorious: Boolean,
  val active: Boolean,
  val banned: Boolean,
  val slaps: List<CyberName>
) {
  val structName: String = "mosaic_struct"

  val getId: Long
    @LongCompress
    get() = id

  val getCreator: CyberName
    @CyberNameCompress
    get() = creator

  val getTracery: Long
    @LongCompress
    get() = tracery

  val getOpus: CyberName
    @CyberNameCompress
    get() = opus

  val getRoyalty: Short
    @ShortCompress
    get() = royalty

  val getCreated: CyberTimeStampMicroseconds
    @LongCompress
    get() = created

  val getGemCount: Short
    @ShortCompress
    get() = gem_count

  val getPoints: Long
    @LongCompress
    get() = points

  val getShares: Long
    @LongCompress
    get() = shares

  val getDamnPoints: Long
    @LongCompress
    get() = damn_points

  val getDamnShares: Long
    @LongCompress
    get() = damn_shares

  val getPledgePoints: Long
    @LongCompress
    get() = pledge_points

  val getReward: Long
    @LongCompress
    get() = reward

  val getCommRating: Long
    @LongCompress
    get() = comm_rating

  val getLeadRating: Long
    @LongCompress
    get() = lead_rating

  val getMeritorious: Boolean
    @BoolCompress
    get() = meritorious

  val getActive: Boolean
    @BoolCompress
    get() = active

  val getBanned: Boolean
    @BoolCompress
    get() = banned

  val getSlaps: List<CyberName>
    @CyberNameCollectionCompress
    get() = slaps

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishMosaicStructComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
