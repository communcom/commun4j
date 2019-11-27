import io.golos.commun4j.Commun4j
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import org.junit.Before
import org.junit.Test
import java.util.*

class RandomTests {
    private lateinit var client: Commun4j

    @Before
    fun before() {
        client = getClient(authInServices = true)
    }

    @Test
    fun testUpdateMeta() {
        client.updateUserMetadata(
                avatarUrl = UUID.randomUUID().toString(),
                coverUrl = UUID.randomUUID().toString(),
                biography = UUID.randomUUID().toString(),
                facebook = UUID.randomUUID().toString(),
                telegram = null,
                whatsapp = UUID.randomUUID().toString(),
                wechat = null,
                bandWidthRequest = BandWidthRequest.bandWidthFromComn,
                clientAuthRequest = ClientAuthRequest.empty).getOrThrow()
    }
}
