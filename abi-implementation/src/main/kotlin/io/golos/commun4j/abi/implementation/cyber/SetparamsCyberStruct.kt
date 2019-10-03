// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class SetparamsCyberStruct(
  val params: ChainConfigCyberStruct
) {
  val structName: String = "setparams"

  val getParams: ChainConfigCyberStruct
    @ChildCompress
    get() = params

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishSetparamsCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
