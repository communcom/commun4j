// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ByteCompress
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.LongCollectionCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.SymbolCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberSymbol
import kotlin.Byte
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ParamStructComnGalleryStruct(
  val id: Long,
  val commun_symbol: CyberSymbol,
  val collection_period: Long,
  val evaluation_period: Long,
  val mosaic_active_period: Long,
  val max_royalty: Short,
  val ban_threshold: Byte,
  val rewarded_num: Byte,
  val comm_points_grade_sum: Long,
  val comm_grades: List<Long>,
  val lead_grades: List<Long>,
  val opuses: List<OpusInfoComnGalleryStruct>
) {
  val structName: String = "param_struct"

  val getId: Long
    @LongCompress
    get() = id

  val getCommunSymbol: CyberSymbol
    @SymbolCompress
    get() = commun_symbol

  val getCollectionPeriod: Long
    @LongCompress
    get() = collection_period

  val getEvaluationPeriod: Long
    @LongCompress
    get() = evaluation_period

  val getMosaicActivePeriod: Long
    @LongCompress
    get() = mosaic_active_period

  val getMaxRoyalty: Short
    @ShortCompress
    get() = max_royalty

  val getBanThreshold: Byte
    @ByteCompress
    get() = ban_threshold

  val getRewardedNum: Byte
    @ByteCompress
    get() = rewarded_num

  val getCommPointsGradeSum: Long
    @LongCompress
    get() = comm_points_grade_sum

  val getCommGrades: List<Long>
    @LongCollectionCompress
    get() = comm_grades

  val getLeadGrades: List<Long>
    @LongCollectionCompress
    get() = lead_grades

  val getOpuses: List<OpusInfoComnGalleryStruct>
    @CollectionCompress
    get() = opuses

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishParamStructComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
