package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

@JsonClass(generateAdapter = true)
class ResolvedProfile(var userId: CyberName, var username: String?, var avatarUrl: String?)