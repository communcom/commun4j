// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.StringCompress
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
data class ReblogComnGalleryStruct(
  val commun_code: CyberSymbolCode,
  val rebloger: CyberName,
  val message_id: MssgidComnGalleryStruct,
  val headermssg: String,
  val bodymssg: String
) {
  val structName: String = "reblog"

  val getCommunCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = commun_code

  val getRebloger: CyberName
    @CyberNameCompress
    get() = rebloger

  val getMessageId: MssgidComnGalleryStruct
    @ChildCompress
    get() = message_id

  val getHeadermssg: String
    @StringCompress
    get() = headermssg

  val getBodymssg: String
    @StringCompress
    get() = bodymssg

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishReblogComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
