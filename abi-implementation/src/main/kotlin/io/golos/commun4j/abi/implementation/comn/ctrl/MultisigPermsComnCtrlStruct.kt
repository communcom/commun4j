// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.ctrl

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ShortCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Int
import kotlin.Short
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class MultisigPermsComnCtrlStruct(
  val super_majority: Short,
  val majority: Short,
  val minority: Short
) : CtrlParamComnCtrlInterface {
  val structName: String = "multisig_perms"

  val getSuperMajority: Short
    @ShortCompress
    get() = super_majority

  val getMajority: Short
    @ShortCompress
    get() = majority

  val getMinority: Short
    @ShortCompress
    get() = minority

  override fun getStructIndexForCollectionSquish(): Int = 2
  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishMultisigPermsComnCtrlStruct(this)
                 .toHex()
  override fun squish() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishMultisigPermsComnCtrlStruct(this)
                 .toBytes()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
