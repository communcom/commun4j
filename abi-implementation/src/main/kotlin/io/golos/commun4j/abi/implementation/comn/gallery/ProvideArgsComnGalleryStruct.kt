// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.AssetCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.NullableCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberAsset
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class ProvideArgsComnGalleryStruct(
  val grantor: CyberName,
  val recipient: CyberName,
  val quantity: CyberAsset,
  val fee: Short?
) {
  val structName: String = "provide_args"

  val getGrantor: CyberName
    @CyberNameCompress
    get() = grantor

  val getRecipient: CyberName
    @CyberNameCompress
    get() = recipient

  val getQuantity: CyberAsset
    @AssetCompress
    get() = quantity

  val getFee: Short?
    @ShortCompress
    @NullableCompress
    get() = fee

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishProvideArgsComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
