//import io.golos.commun4j.Commun4j
//import io.golos.commun4j.services.model.EventType
//import io.golos.commun4j.services.model.MobileShowSettings
//import io.golos.commun4j.services.model.NotificationSettings
//import io.golos.commun4j.services.model.ServiceSettingsLanguage
//import io.golos.commun4j.utils.StringSigner
//import io.golos.commun4j.sharedmodel.Commun4jConfig
//import io.golos.commun4j.sharedmodel.Either
//import junit.framework.Assert.assertEquals
//import junit.framework.Assert.assertTrue
//import org.junit.Test
//import java.util.*
//
//class NotifsTest {
//    val client = Commun4j(Commun4jConfig(blockChainHttpApiUrl = "http://46.4.96.246:8888/",
//            servicesUrl = "ws://116.203.98.241:8080"))
//
//    val userId = (client.resolveCanonicalCyberName("cbfjdhdhjdh") as Either.Success).value.userId
//    val key = "5JTZuS3VANMazmS3ZXCMXUrUJp6Qwv8HUMzxjX1SURa64w86AgL"
//
//    @Test
//    fun tesGetNotifs() {
//        val secret = client.getAuthSecret().getOrThrow()
//        client.authWithSecret(userId.name,
//                secret.secret,
//                StringSigner.signString(secret.secret, key)).getOrThrow()
//
//        assertTrue(client.isUserAuthed().getOrThrow())
//
//        val deviceId = UUID.randomUUID().toString()
//        val fcmKey = UUID.randomUUID().toString()
//
//        val subscriptionResult = client.subscribeOnMobilePushNotifications(deviceId, fcmKey)
//        assertTrue(subscriptionResult is Either.Success)
//
//        val unSubscriptionResult = client.unSubscribeOnNotifications(userId, deviceId)
//        assertTrue(unSubscriptionResult is Either.Success)
//
//        val unreadCount = client.getFreshNotificationCount(userId.name)
//        assertTrue(unreadCount is Either.Success)
//        assertTrue((unreadCount as Either.Success).value.fresh > -1)
//
//        val marksAsREad = client.markAllEventsAsNotFresh("gls")
//        assertTrue(marksAsREad is Either.Success)
//
//        val events = client.getEvents(userId.name,
//                null, 100, false, false, EventType.values().toList(), "gls")
//        assertTrue(events is Either.Success)
//        assertTrue((events as Either.Success).value.data != null)
//        assertTrue(events.value.fresh > -1)
//        assertTrue(events.value.total > -1)
//
//
//        val mobileSettings = MobileShowSettings(NotificationSettings(true, true, true, true, true, true,
//                false, true, false, false, false, false), ServiceSettingsLanguage.RUSSIAN)
//
//        val setSettingsResult = client.setUserSettings(deviceId,
//                null, null, mobileSettings, "gls")
//        assertTrue(setSettingsResult is Either.Success)
//
//        val getSettingsResult = client.getUserSettings(deviceId, "gls")
//        assertTrue(getSettingsResult is Either.Success)
//
//        assertEquals(mobileSettings, (getSettingsResult as Either.Success).value.push)
//
//    }
//}