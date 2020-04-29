package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

@JsonClass(generateAdapter = true)
data class VerifyStepResult(val currentState: String)

@JsonClass(generateAdapter = true)
data class SetUserNameStepResult(val currentState: String, val userId: CyberName)

