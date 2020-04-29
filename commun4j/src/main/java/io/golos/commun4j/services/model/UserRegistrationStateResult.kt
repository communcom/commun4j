package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
import io.golos.commun4j.sharedmodel.CyberName

@JsonClass(generateAdapter = true)
data class UserRegistrationStateResult(val currentState: String, val data: UserData?){
    @JsonClass(generateAdapter = true)
    data class UserData(val userId: CyberName, val username: String)
}
