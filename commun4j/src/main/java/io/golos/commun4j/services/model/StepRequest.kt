package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

@JsonClass(generateAdapter = true)
data class VerifyStepResult(val currentState: UserRegistrationState)

@JsonClass(generateAdapter = true)
data class SetUserNameStepResult(val currentState: UserRegistrationState, val userId: CyberName)

