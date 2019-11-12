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


//User with lot of money is ready:
//Username == spencer-gisele-dds
//userid == tst2uqbzwwyi
//active_key == 5JejyGNE7wt5XSVHySJkzJTicnMGfYU4MWGrjQLUDcECyP95HMo
//owner_key == 5JMnXGhU3K4b4sgbQxNnBbPxvEYV171Bez8b8Zq191ViE4ChtGm
//password == JhqpsgEzMbogRZhEJYlkqWoLjkRRRtVesmBlUvhYdRXVgTTFreFA

class PostingTest {
    private lateinit var client: Commun4j
    val userId = "tst2uqbzwwyi".toCyberName()
    val userName = "spencer-gisele-dds"
    val userPrivateKey = "5JejyGNE7wt5XSVHySJkzJTicnMGfYU4MWGrjQLUDcECyP95HMo"

    val provideBwKey = "5JdhhMMJdb1KEyCatAynRLruxVvi7mWPywiSjpLYqKqgsT4qjsN"

    @Before
    fun before() {
        client = getClient(setActiveUser = false, authInServices = false)
    }

    @Test
    fun postingTest() {

        client.getUserProfile(null, userName)

        val secret = client.getAuthSecret().getOrThrow().secret
        client.authWithSecret(userName, secret, StringSigner.signString(secret, userPrivateKey)).getOrThrow()

        client.createPost(CyberSymbolCode(client.getCommunitiesList(CyberName("lol"), 0, 1).getOrThrow().items.first().communityId),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                emptyList(),
                "",
                1.toShort(),
                BandWidthRequest.bandWidthFromComn,
                null,
                userId,
                userPrivateKey).getOrThrow()
    }

    @Test
    fun postingWithProvideBwKey() {
        client.createPost(CyberSymbolCode(client.getCommunitiesList(CyberName("lol"), 0, 1).getOrThrow().items.first().communityId),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                emptyList(),
                "",
                1.toShort(),
                BandWidthRequest.bandwidthFromComnUsingItsKey(EosPrivateKey(provideBwKey)),
                ClientAuthRequest.createComnAuthRequest(EosPrivateKey("5JdhhMMJdb1KEyCatAynRLruxVvi7mWPywiSjpLYqKqgsT4qjsN")),
                userId,
                userPrivateKey).getOrThrow()
    }

    @Test
    fun test1() {
        val user = CyberName("tst2irerblkd")
        val ccgs = CreateCGalleryStruct(CyberSymbolCode("MINEC"), MssgidCGalleryStruct(user, UUID.randomUUID().toString()),
                MssgidCGalleryStruct(CyberName(), ""), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                emptyList(), "", 4)
        val ccga = CreateCGalleryAction(ccgs)
        val create = ccga.push(listOf(TransactionAuthorizationAbi(user.name, "active")),
                Collections.singletonList(EosPrivateKey("5J7B2kBnAHacCpZYEsqbdcMtV7KAvKWTVfHhj1Qv91CSgMat8qZ")),
                Commun4jConfig(BuildConfig.BLOCKCHAIN_DEV, BuildConfig.SERVICES_DEV),
                BandWidthProvideOption(listOf(BandwidthProviding("c".toCyberName(),
                        user),
                        BandwidthProviding("c".toCyberName(),
                                "c.gallery".toCyberName())), listOf(EosPrivateKey(BuildConfig.CREATE_KEY))))

        create.getOrThrow()
    }
}