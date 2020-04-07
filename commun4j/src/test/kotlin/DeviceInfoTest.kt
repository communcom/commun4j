import org.junit.Test
import java.util.*

class DeviceInfoTest {
    private val client = getClient(authInServices = true)

    @Test
    fun setDeviceInfoAndFcmTokenTest() {
        client.setInfo(-180).getOrThrow()

    }

    @Test
    fun setFcmTokenTest() {
        client.setFcmToken(UUID.randomUUID().toString()).getOrThrow()
        client.resetFcmToken().getOrThrow()
    }
}