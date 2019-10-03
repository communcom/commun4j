// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.IntCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Int
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class VertexStructComnGalleryStruct(
  val id: Long,
  val creator: CyberName,
  val tracery: Long,
  val parent_creator: CyberName,
  val parent_tracery: Long,
  val level: Short,
  val childcount: Int
) {
  val structName: String = "vertex_struct"

  val getId: Long
    @LongCompress
    get() = id

  val getCreator: CyberName
    @CyberNameCompress
    get() = creator

  val getTracery: Long
    @LongCompress
    get() = tracery

  val getParentCreator: CyberName
    @CyberNameCompress
    get() = parent_creator

  val getParentTracery: Long
    @LongCompress
    get() = parent_tracery

  val getLevel: Short
    @ShortCompress
    get() = level

  val getChildcount: Int
    @IntCompress
    get() = childcount

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishVertexStructComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
