import io.golos.commun4j.services.model.GetNotificationsFilter
import io.golos.commun4j.services.model.NotificationType
import org.junit.Test

class NotificationsTest {
    val client = getClient(authInServices = true)

    @Test
    fun testNotifications() {
        val ntfs = client.getNotifications(limit = 170, filter = listOf(GetNotificationsFilter.REFERRAL_PURCH_BONUS, GetNotificationsFilter.REFERRAL_REG_BONUS)).getOrThrow()
        ntfs.forEach { println(it.timestamp) }
    }

    @Test
    fun setPushSettings() {
        client.setPushSettings(listOf(NotificationType.REFERRAL_PURCH_BONUS, NotificationType.REFERRAL_REG_BONUS)).getOrThrow()
        client.getPushSettings().getOrThrow()
    }
}