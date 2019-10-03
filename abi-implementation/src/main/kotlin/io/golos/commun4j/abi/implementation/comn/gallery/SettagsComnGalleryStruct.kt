// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.StringCollectionCompress
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
data class SettagsComnGalleryStruct(
  val commun_code: CyberSymbolCode,
  val leader: CyberName,
  val message_id: MssgidComnGalleryStruct,
  val add_tags: List<String>,
  val remove_tags: List<String>,
  val reason: String
) {
  val structName: String = "settags"

  val getCommunCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = commun_code

  val getLeader: CyberName
    @CyberNameCompress
    get() = leader

  val getMessageId: MssgidComnGalleryStruct
    @ChildCompress
    get() = message_id

  val getAddTags: List<String>
    @StringCollectionCompress
    get() = add_tags

  val getRemoveTags: List<String>
    @StringCollectionCompress
    get() = remove_tags

  val getReason: String
    @StringCompress
    get() = reason

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishSettagsComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
