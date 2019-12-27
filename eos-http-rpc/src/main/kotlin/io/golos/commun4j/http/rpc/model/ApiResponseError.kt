package io.golos.commun4j.http.rpc.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponseError(val id: Long,
                            val error: ErrorDetails)

@JsonClass(generateAdapter = true)
data class ErrorDetails(val code: Long, val message: String, val error: Any?, val data: Any?)