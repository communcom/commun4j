package io.golos.commun4j.http.rpc.model.account.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountAuth(
    val permission: AccountAuthPermission,
    val weight: Int
)