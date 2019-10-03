// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.list

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.StringCompress
import io.golos.commun4j.abi.writer.SymbolCodeCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class CommunityComnListStruct(
  val token_name: CyberSymbolCode,
  val community_name: String
) {
  val structName: String = "community"

  val getTokenName: CyberSymbolCode
    @SymbolCodeCompress
    get() = token_name

  val getCommunityName: String
    @StringCompress
    get() = community_name

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishCommunityComnListStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
