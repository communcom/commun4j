// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.point

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.AssetCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
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
data class ParamStructComnPointStruct(
  val max_supply: CyberAsset,
  val cw: Short,
  val fee: Short,
  val issuer: CyberName
) {
  val structName: String = "param_struct"

  val getMaxSupply: CyberAsset
    @AssetCompress
    get() = max_supply

  val getCw: Short
    @ShortCompress
    get() = cw

  val getFee: Short
    @ShortCompress
    get() = fee

  val getIssuer: CyberName
    @CyberNameCompress
    get() = issuer

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishParamStructComnPointStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
