// Class is generated, changes would be overridden on compile
package io.golos.commun4j.abi.implementation.comn.social

import com.squareup.moshi.JsonClass
import io.golos.commun4j.abi.implementation.AbiBinaryGenCyber
import io.golos.commun4j.abi.writer.Abi
import io.golos.commun4j.abi.writer.NullableCompress
import io.golos.commun4j.abi.writer.StringCompress
import io.golos.commun4j.abi.writer.compression.CompressionType
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlin.String
import kotlin.collections.List

@Abi
@JsonClass(generateAdapter = true)
data class AccountmetaComnSocialStruct(
  val avatar_url: String?,
  val cover_url: String?,
  val biography: String?,
  val facebook: String?,
  val telegram: String?,
  val whatsapp: String?,
  val wechat: String?
) {
  val structName: String = "accountmeta"

  val getAvatarUrl: String?
    @StringCompress
    @NullableCompress
    get() = avatar_url

  val getCoverUrl: String?
    @StringCompress
    @NullableCompress
    get() = cover_url

  val getBiography: String?
    @StringCompress
    @NullableCompress
    get() = biography

  val getFacebook: String?
    @StringCompress
    @NullableCompress
    get() = facebook

  val getTelegram: String?
    @StringCompress
    @NullableCompress
    get() = telegram

  val getWhatsapp: String?
    @StringCompress
    @NullableCompress
    get() = whatsapp

  val getWechat: String?
    @StringCompress
    @NullableCompress
    get() = wechat

  fun toHex() = AbiBinaryGenCyber(CompressionType.NONE)
                 .squishAccountmetaComnSocialStruct(this)
                 .toHex()
  fun toActionAbi(
    contractName: String,
    actionName: String,
    transactionAuth: List<TransactionAuthorizationAbi>
  ) = ActionAbi(contractName, actionName,
         transactionAuth, toHex())}
