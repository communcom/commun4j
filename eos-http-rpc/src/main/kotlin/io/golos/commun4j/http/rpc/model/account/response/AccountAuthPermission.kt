package io.golos.commun4j.http.rpc.model.account.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountAuthPermission(
    val actor: String,
    val permission: String
)