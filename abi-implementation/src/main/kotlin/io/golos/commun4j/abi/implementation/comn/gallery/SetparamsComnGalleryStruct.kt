// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.InterfaceCollectionCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class SetparamsComnGalleryStruct(
  val commun_code: CyberSymbolCode,
  val params: List<PostingParamComnGalleryInterface>
) {
  val structName: String = "setparams"

  val getCommunCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = commun_code

  val getParams: List<PostingParamComnGalleryInterface>
    @InterfaceCollectionCompress
    get() = params

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishSetparamsComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
