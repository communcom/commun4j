package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class FirstRegistrationStepRequest(val captcha: String?,
                                            val phone: String,
                                            val testingPass: String?)

@JsonClass(generateAdapter = true)
internal class FirstRegistrationStepEmailRequest(val captcha: String?,
                                                 val email: String,
                                                 val testingPass: String?)