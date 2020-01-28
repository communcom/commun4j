import io.golos.commun4j.services.model.GetNotificationsFilter
import org.junit.Test

class NotificationsTest {
    @Test
    fun testNotifications() {
        val client = getClient(authInServices = true)

        client.getNotifications(10).getOrThrow()
        client.getNotifications(filter = GetNotificationsFilter.values().toList()).getOrThrow()
        client.getNotifications(filter = listOf(GetNotificationsFilter.MENTION)).getOrThrow()

        client.getNotificationsStatus().getOrThrow()
        client.markAllNotificationAsViewed(client.getNotifications().getOrThrow().lastNotificationTimestamp!!).getOrThrow()
    }
}