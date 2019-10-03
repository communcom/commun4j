// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CollectionCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class AdviceStructComnGalleryStruct(
  val id: Long,
  val leader: CyberName,
  val favorites: List<MosaicKeyComnGalleryStruct>
) {
  val structName: String = "advice_struct"

  val getId: Long
    @LongCompress
    get() = id

  val getLeader: CyberName
    @CyberNameCompress
    get() = leader

  val getFavorites: List<MosaicKeyComnGalleryStruct>
    @CollectionCompress
    get() = favorites

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishAdviceStructComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
