// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class PostingStateComnGalleryStruct(
  val id: Long,
  val max_comment_depth: StMaxCommentDepthComnGalleryStruct
) {
  val structName: String = "posting_state"

  val getId: Long
    @LongCompress
    get() = id

  val getMaxCommentDepth: StMaxCommentDepthComnGalleryStruct
    @ChildCompress
    get() = max_comment_depth

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishPostingStateComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
