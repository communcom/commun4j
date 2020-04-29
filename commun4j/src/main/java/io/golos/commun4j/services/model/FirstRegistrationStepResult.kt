package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class FirstRegistrationStepResult(val code: Int?,
                                       val currentState: String,
                                       val nextSmsRetry: Date)


@JsonClass(generateAdapter = true)
data class FirstRegistrationStepEmailResult(val code: String?,
                                       val currentState: String,
                                       val nextEmailRetry: Date)