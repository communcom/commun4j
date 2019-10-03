// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.NullableCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class TransferArgsComnGalleryStruct(
  val commun_code: CyberSymbolCode,
  val message_id: MssgidComnGalleryStruct,
  val gem_owner: CyberName,
  val gem_creator: CyberName?,
  val recipient: CyberName
) {
  val structName: String = "transfer_args"

  val getCommunCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = commun_code

  val getMessageId: MssgidComnGalleryStruct
    @ChildCompress
    get() = message_id

  val getGemOwner: CyberName
    @CyberNameCompress
    get() = gem_owner

  val getGemCreator: CyberName?
    @CyberNameCompress
    @NullableCompress
    get() = gem_creator

  val getRecipient: CyberName
    @CyberNameCompress
    get() = recipient

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishTransferArgsComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
