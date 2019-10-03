// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.ctrl

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.ChildCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class CtrlStateComnCtrlStruct(
  val id: Long,
  val multisig: MultisigAccComnCtrlStruct,
  val witnesses: MaxWitnessesComnCtrlStruct,
  val msig_perms: MultisigPermsComnCtrlStruct,
  val witness_votes: MaxWitnessVotesComnCtrlStruct
) {
  val structName: String = "ctrl_state"

  val getId: Long
    @LongCompress
    get() = id

  val getMultisig: MultisigAccComnCtrlStruct
    @ChildCompress
    get() = multisig

  val getWitnesses: MaxWitnessesComnCtrlStruct
    @ChildCompress
    get() = witnesses

  val getMsigPerms: MultisigPermsComnCtrlStruct
    @ChildCompress
    get() = msig_perms

  val getWitnessVotes: MaxWitnessVotesComnCtrlStruct
    @ChildCompress
    get() = witness_votes

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishCtrlStateComnCtrlStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
