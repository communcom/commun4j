import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.utils.StringSigner
import org.junit.Assert.assertTrue
import org.junit.Test

class ServicesFetchTest {
    private val client = getClient(CONFIG_TYPE.DEV, false, false)

    @Test
    fun getCommunititeTest() {
        val getCommunitiesResult = client.getCommunitiesList(CyberName("lol"), 0, 20)
        assertTrue(getCommunitiesResult is Either.Success)

        val community = (getCommunitiesResult as Either.Success).value.items[0]

        val getComnityResult = client.getCommunity(community.id, CyberName("lol"))

        assertTrue(getComnityResult is Either.Success)
    }

    @Test
    fun userMetadataFetchTest() {
        val response = client
                .getUserProfile(CyberName("tst4vjjdvryc"), null)
        assertTrue(response is Either.Success)

        val canonicalName = client.resolveCanonicalCyberName("johnston-yaeko-i")
        assertTrue(canonicalName is Either.Success)
    }

    @Test
    fun testAuth(){
        val secret = client.getAuthSecret().getOrThrow().secret
        client.authWithSecret(
                "stanton-hayden-phd",
                secret,
                StringSigner.signString(secret, "5KNygaB5K31Mhy56gA4uxV2VGgkH1XdWEKLKG3wLW5XTYPAFi13")
        ).getOrThrow()
    }

    @Test
    fun getPost() {
        val posts = client.getPosts() as Either.Success
        val firstItem = posts.value.items.first()

        val getPostResp = client.getPost(firstItem.author.userId, firstItem.community.id, firstItem.contentId.permlink)
        assertTrue(getPostResp is Either.Success)

        val postsRaw = client.getPostsRaw() as Either.Success
        val firstItemRaw = postsRaw.value.items.first()

        val getPostRespRaw = client.getPostRaw(firstItemRaw.author.userId, firstItemRaw.community.id, firstItemRaw.contentId.permlink)
        assertTrue(getPostRespRaw is Either.Success)
    }
}