package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

@JsonClass(generateAdapter = true)
data class AuthResult(val user: String, val username: String?, val userId: CyberName,
                      val permission: String)
