// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCollectionCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
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
data class SetprovidersArgsComnGalleryStruct(
  val commun_code: CyberSymbolCode,
  val recipient: CyberName,
  val providers: List<CyberName>
) {
  val structName: String = "setproviders_args"

  val getCommunCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = commun_code

  val getRecipient: CyberName
    @CyberNameCompress
    get() = recipient

  val getProviders: List<CyberName>
    @CyberNameCollectionCompress
    get() = providers

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishSetprovidersArgsComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
