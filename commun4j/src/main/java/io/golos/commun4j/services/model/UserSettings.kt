package io.golos.commun4j.services.model

import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class WebShowSettings(
        val show: NotificationSettings?)
@JsonClass(generateAdapter = true)
data class MobileShowSettings(val show: NotificationSettings?,
                              val lang: ServiceSettingsLanguage?)

@JsonClass(generateAdapter = true)
data class NotificationSettings(val upvote: Boolean?,
                                val downvote: Boolean?,
                                val reply: Boolean?,
                                val transfer: Boolean?,
                                val subscribe: Boolean?,
                                val unsubscribe: Boolean?,
                                val mention: Boolean?,
                                val repost: Boolean?,
                                val witnessVote: Boolean?,
                                val witnessCancelVote: Boolean?,
                                val reward: Boolean?,
                                val curatorReward: Boolean?)

enum class ServiceSettingsLanguage {
    ENGLISH, RUSSIAN;

    override fun toString(): String {
        return when (this) {
            ENGLISH -> "en"
            RUSSIAN -> "ru"
        }
    }
}

@JsonClass(generateAdapter = true)
class UserSettings(
        //device userId
        val profile: String?,
        val app:String?,
        val basic: Any?,
        val notify: WebShowSettings?,
        val push: MobileShowSettings?
)
@JsonClass(generateAdapter = true)
internal class ServicesSettingsRequest(
        val profile: String,
        val app:String)