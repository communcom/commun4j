package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class VerifyPhoneRequest(val phone: String, val code: Int)

@JsonClass(generateAdapter = true)
internal class VerifyEmailRequest(val email: String, val code: Int)