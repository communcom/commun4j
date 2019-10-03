// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.cyber

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.BoolCompress
import io.golos.commun4j.abi.writer.ByteCompress
import io.golos.commun4j.abi.writer.BytesCompress
import io.golos.commun4j.abi.writer.CheckSumCompress
import io.golos.commun4j.abi.writer.CyberNameCompress
import io.golos.commun4j.abi.writer.LongCompress
import io.golos.commun4j.abi.writer.StringCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.sharedmodel.CheckSum256
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberTimeStampMicroseconds
import kotlin.Boolean
import kotlin.Byte
import kotlin.ByteArray
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class AccountObjectCyberStruct(
  val name: CyberName,
  val vm_type: Byte,
  val vm_version: Byte,
  val privileged: Boolean,
  val last_code_update: CyberTimeStampMicroseconds,
  val code_version: CheckSum256,
  val abi_version: CheckSum256,
  val creation_date: String,
  val code: String,
  val abi: ByteArray
) {
  val structName: String = "account_object"

  val getName: CyberName
    @CyberNameCompress
    get() = name

  val getVmType: Byte
    @ByteCompress
    get() = vm_type

  val getVmVersion: Byte
    @ByteCompress
    get() = vm_version

  val getPrivileged: Boolean
    @BoolCompress
    get() = privileged

  val getLastCodeUpdate: CyberTimeStampMicroseconds
    @LongCompress
    get() = last_code_update

  val getCodeVersion: CheckSum256
    @CheckSumCompress
    get() = code_version

  val getAbiVersion: CheckSum256
    @CheckSumCompress
    get() = abi_version

  val getCreationDate: String
    @StringCompress
    get() = creation_date

  val getCode: String
    @StringCompress
    get() = code

  val getAbi: ByteArray
    @BytesCompress
    get() = abi

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishAccountObjectCyberStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
