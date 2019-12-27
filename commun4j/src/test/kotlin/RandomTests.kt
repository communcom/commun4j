import com.squareup.moshi.Moshi
import io.golos.commun4j.Commun4j
import io.golos.commun4j.abi.implementation.c.gallery.MssgidCGalleryStruct
import io.golos.commun4j.chain.actions.transaction.abi.ActionAbi
import io.golos.commun4j.chain.actions.transaction.abi.TransactionAuthorizationAbi
import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.ClientAuthRequest
import io.golos.commun4j.model.FeedType
import io.golos.commun4j.model.resolveActions
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import io.golos.commun4j.sharedmodel.GolosEosError
import io.golos.commun4j.utils.StringSigner
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.*

class RandomTests {
    private lateinit var client: Commun4j

    @Before
    fun before() {
        client = getClient(authInServices = false)
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

    @Test
    fun testReport() {
        val reporter = "cmn5bzqfmjtw".toCyberName()
        val userName = "kirlin-lenita-iii"
        val activeKey = "5JAGy2NbZTgDYMb59QKA5YdbSkzvhg9Y4YhriudPk8nvGFr8acs"

        val secret = client.getAuthSecret().getOrThrow().secret

        client.authWithSecret(userName, secret, StringSigner.signString(secret, activeKey)).getOrThrow()

        val post = client.getPosts(type = FeedType.NEW, limit = 1).getOrThrow().first()

        client.reportContent(CyberSymbolCode(post.community.communityId),
                MssgidCGalleryStruct(post.author.userId, post.contentId.permlink),
                "[\"NSFW\"]",
                BandWidthRequest.bandWidthFromComn,
                ClientAuthRequest.empty,
                reporter,
                activeKey)
                .getOrThrow()
    }
}


