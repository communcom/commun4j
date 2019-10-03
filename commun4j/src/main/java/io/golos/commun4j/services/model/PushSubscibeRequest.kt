package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class PushSubscibeRequest(val key: String,
                                   val profile: String,
                                   val app:String)

@JsonClass(generateAdapter = true)
internal class PushUnSubscibeRequest(val user: String,
                                   val profile: String,
                                   val app:String)