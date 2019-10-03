// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Int
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class StMaxCommentDepthComnGalleryStruct(
  val value: Short
) : PostingParamComnGalleryInterface {
  val structName: String = "st_max_comment_depth"

  val getValue: Short
    @ShortCompress
    get() = value

  override fun getStructIndexForCollectionSquish(): Int = 0
  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishStMaxCommentDepthComnGalleryStruct(this)
                 .toHex()
  override fun squish() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishStMaxCommentDepthComnGalleryStruct(this)
                 .toBytes()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
