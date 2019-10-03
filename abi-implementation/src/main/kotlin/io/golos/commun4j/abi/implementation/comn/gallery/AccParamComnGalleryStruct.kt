// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCollectionCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class AccParamComnGalleryStruct(
  val account: CyberName,
  val actions_per_day: Short,
  val providers: List<CyberName>
) {
  val structName: String = "acc_param"

  val getAccount: CyberName
    @CyberNameCompress
    get() = account

  val getActionsPerDay: Short
    @ShortCompress
    get() = actions_per_day

  val getProviders: List<CyberName>
    @CyberNameCollectionCompress
    get() = providers

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishAccParamComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
