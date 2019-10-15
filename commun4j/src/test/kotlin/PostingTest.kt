import io.golos.commun4j.Commun4j
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.BandWidthSource
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.commun4j.utils.StringSigner
import org.junit.Before
import org.junit.Test
import java.util.*

class PostingTest {
    private lateinit var client: Commun4j

    @Before
    fun before() {
        client = getClient(setActiveUser = false, authInServices = false)
    }

    @Test
    fun postingTest() {
        val user = "tst3ugfrothv".toCyberName()
        val activeKey = "5JQPHFYBopLg2ga5rQhhf8phGaj9fk5g2vk23H6pNeScwuoHqf3"

        val secret = client.getAuthSecret().getOrThrow().secret
        client.authWithSecret(user.name, secret, StringSigner.signString(secret, activeKey)).getOrThrow()

        client.createPost(CyberSymbolCode(client.getCommunitiesList(CyberName("lol"), 0, 20).getOrThrow().items.first().code),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                emptyList(),
                "",
                0.toShort(),
                null,
                BandWidthRequest(BandWidthSource.COMN_SERVICES, "gls".toCyberName()),
                user,
                activeKey).getOrThrow()
    }

}