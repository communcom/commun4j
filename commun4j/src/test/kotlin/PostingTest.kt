import io.golos.commun4j.BuildConfig
import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.BandWidthProvideOption
import io.golos.commun4j.abi.implementation.BandwidthProviding
import io.golos.commun4j.abi.implementation.c.gallery.CreateCGalleryAction
import io.golos.commun4j.abi.implementation.c.gallery.CreateCGalleryStruct
import io.golos.commun4j.abi.implementation.c.gallery.MssgidCGalleryStruct
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.core.crypto.EosPrivateKey
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import io.golos.commun4j.sharedmodel.Commun4jConfig
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
        client = getClient(authInServices = true)
    }

    @Test
    fun postingTest() {
        client.createPost(CyberSymbolCode(client.getCommunitiesList(limit = 1).getOrThrow().items.first().communityId),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                emptyList(),
                "",
                10.toShort(),
                BandWidthRequest.bandWidthFromComn,
                null).getOrThrow()
    }

    @Test
    fun postingWithProvideBwKey() {
        client.createPost(CyberSymbolCode(client.getCommunitiesList(limit = 1).getOrThrow().items.first().communityId),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                emptyList(),
                "",
                1.toShort(),
                BandWidthRequest.bandwidthFromComnUsingItsKey(EosPrivateKey(BuildConfig.CREATE_KEY))).getOrThrow()
    }

}