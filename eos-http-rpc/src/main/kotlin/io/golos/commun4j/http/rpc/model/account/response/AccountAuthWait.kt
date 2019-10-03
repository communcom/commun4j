package io.golos.commun4j.http.rpc.model.account.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountAuthWait(
    val wait_sec: Long,
    val weight: Int
)