import io.golos.commun4j.services.model.NotificationType
import org.junit.Test

class NotificationsTest {
    val client = getClient(authInServices = true)

    @Test
    fun testNotifications() {
        val ntfs = client.getNotificationsSkipUnrecognized(limit = 170).getOrThrow()
        ntfs.forEach { println(it.timestamp) }
    }

    @Test
    fun setPushSettings() {
        client.setPushSettings(listOf(NotificationType.MENTION)).getOrThrow()
        client.getPushSettings().getOrThrow()
    }
}