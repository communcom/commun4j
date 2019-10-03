// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber.stake

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
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
data class ClaimArgsCyberStakeStruct(
  val grantor_name: CyberName,
  val recipient_name: CyberName,
  val token_code: CyberSymbolCode
) {
  val structName: String = "claim_args"

  val getGrantorName: CyberName
    @CyberNameCompress
    get() = grantor_name

  val getRecipientName: CyberName
    @CyberNameCompress
    get() = recipient_name

  val getTokenCode: CyberSymbolCode
    @SymbolCodeCompress
    get() = token_code

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishClaimArgsCyberStakeStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
