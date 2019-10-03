// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.ctrl

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberName
import kotlin.Int
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class MultisigAccComnCtrlStruct(
  val name: CyberName
) : CtrlParamComnCtrlInterface {
  val structName: String = "multisig_acc"

  val getName: CyberName
    @CyberNameCompress
    get() = name

  override fun getStructIndexForCollectionSquish(): Int = 0
  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishMultisigAccComnCtrlStruct(this)
                 .toHex()
  override fun squish() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishMultisigAccComnCtrlStruct(this)
                 .toBytes()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
