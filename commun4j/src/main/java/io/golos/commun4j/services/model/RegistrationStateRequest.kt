package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RegistrationStateRequest(val email: String?, val phone: String?, val identity: String?)

@JsonClass(generateAdapter = true)
internal data class SetVerifiedUserNameRequest(val username: String, val phone: String?, val identity: String?,
                                               val email: String?)