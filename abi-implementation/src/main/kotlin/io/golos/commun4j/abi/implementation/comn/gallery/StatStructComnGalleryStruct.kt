// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.gallery

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.Long
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class StatStructComnGalleryStruct(
  val id: Long,
  val unclaimed: Long,
  val retained: Long
) {
  val structName: String = "stat_struct"

  val getId: Long
    @LongCompress
    get() = id

  val getUnclaimed: Long
    @LongCompress
    get() = unclaimed

  val getRetained: Long
    @LongCompress
    get() = retained

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishStatStructComnGalleryStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
