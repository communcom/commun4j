import io.golos.commun4j.BuildConfig
import io.golos.commun4j.Commun4j
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import io.golos.commun4j.model.FeedType
import io.golos.commun4j.model.hasBalanceDoesNotExistError
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.utils.StringSigner
import org.junit.Before
import org.junit.Test
import java.util.*


class PostingTest {
    private lateinit var client: Commun4j

    @Before
    fun before() {
        client = getClient(authInServices = false)
    }

    @Test
    fun postingTest() {
        val userName = "akis420-gaming"
        val userId = "cmn5koopmlev".toCyberName()
        val key = "5Jsn7mH7AwCbF13aUZ7p8LwSjHF3RNE4Dupg74EtjZQ4NoFtgfm"

        val secret = client.getAuthSecret().getOrThrow().secret
        client.authWithSecret(userName, secret, StringSigner.signString(secret, key)).getOrThrow()

        val result = client.createPost(CyberSymbolCode(client.getPosts(type = FeedType.HOT, limit = 1, offset = 11).getOrThrow().items.first().community.communityId),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                emptyList(),
                "",
                null,
                BandWidthRequest.bandWidthFromComn,
                ClientAuthRequest.empty,
                userId,
                key)

        result.getOrThrow()
    }

    @Test
    fun postingWithProvideBwKey() {
        client.createPost(CyberSymbolCode(client.getCommunitiesList(limit = 1).getOrThrow().items.first().communityId),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                emptyList(),
                "",
                null,
                BandWidthRequest.bandwidthFromComnUsingItsKey(EosPrivateKey(BuildConfig.CREATE_KEY))).getOrThrow()
    }

}