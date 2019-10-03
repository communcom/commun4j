// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Long
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ProvisionStructComnGalleryStruct(
  val id: Long,
  val grantor: CyberName,
  val recipient: CyberName,
  val fee: Short,
  val total: Long,
  val frozen: Long
) {
  val structName: String = "provision_struct"

  val getId: Long
    @LongCompress
    get() = id

  val getGrantor: CyberName
    @CyberNameCompress
    get() = grantor

  val getRecipient: CyberName
    @CyberNameCompress
    get() = recipient

  val getFee: Short
    @ShortCompress
    get() = fee

  val getTotal: Long
    @LongCompress
    get() = total

  val getFrozen: Long
    @LongCompress
    get() = frozen

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishProvisionStructComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
