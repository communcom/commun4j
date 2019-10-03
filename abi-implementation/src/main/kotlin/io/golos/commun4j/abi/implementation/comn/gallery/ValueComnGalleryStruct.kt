// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ByteCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Byte
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ValueComnGalleryStruct(
  val kind: Byte,
  val idx: Long
) {
  val structName: String = "value"

  val getKind: Byte
    @ByteCompress
    get() = kind

  val getIdx: Long
    @LongCompress
    get() = idx

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishValueComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
