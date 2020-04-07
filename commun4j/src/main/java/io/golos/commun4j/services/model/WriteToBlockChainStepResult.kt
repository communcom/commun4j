package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName
import java.util.*

@JsonClass(generateAdapter = true)
data class WriteToBlockChainStepResult(val username: String, val userId: CyberName, val currentState: UserRegistrationState)

@JsonClass(generateAdapter = true)
data class ResendEmailResult(val nextEmailRetry: Date, val currentState: UserRegistrationState)

@JsonClass(generateAdapter = true)
data class ResendSmsResult(val nextSmsRetry: Date, val currentState: UserRegistrationState)