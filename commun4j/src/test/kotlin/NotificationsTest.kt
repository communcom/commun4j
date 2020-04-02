import io.golos.commun4j.services.model.GetNotificationsFilter
import io.golos.commun4j.services.model.UnsupportedNotification
import org.junit.Test

class NotificationsTest {
    @Test
    fun testNotifications() {

        val client = getClient(authInServices = true)


        val ntfs = client.getNotificationsSkipUnrecognized(limit = 170).getOrThrow()
        ntfs.forEach { println(it.timestamp) }
    }
}