import io.golos.commun4j.model.FeedTimeFrame
import io.golos.commun4j.model.FeedType
import io.golos.commun4j.services.model.TransactionDirection
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

        val getComnityResult = client.getCommunity(community.communityId)

        assertTrue(getComnityResult is Either.Success)
    }

    @Test
    fun userMetadataFetchTest() {
        val response = client
                .getUserProfile("tst2uqbzwwyi".toCyberName(), null)
        assertTrue(response is Either.Success)
        println(response)

        val secret = client.getAuthSecret().getOrThrow().secret
        client.authWithSecret("spencer-gisele-dds", secret, StringSigner.signString(secret, "5JejyGNE7wt5XSVHySJkzJTicnMGfYU4MWGrjQLUDcECyP95HMo")).getOrThrow()

        val posts = client.getPosts(type = FeedType.TOP_LIKES, timeframe = FeedTimeFrame.MONTH, limit = 100).getOrThrow()
        posts.items.forEach {
            client.getUserProfile(it.author.userId, null)
        }
    }

    @Test
    fun testAuth() {
        val secret = client.getAuthSecret().getOrThrow().secret
        client.authWithSecret(
                "stanton-hayden-phd",
                secret,
                StringSigner.signString(secret, "5KNygaB5K31Mhy56gA4uxV2VGgkH1XdWEKLKG3wLW5XTYPAFi13")
        ).getOrThrow()
    }

    @Test
    fun getPost() {
        val posts = client.getPosts(type = FeedType.TOP_LIKES,
                allowNsfw = true, timeframe = FeedTimeFrame.MONTH, limit = 100) as Either.Success
        val firstItem = posts.value.items.first()

        val getPostResp = client.getPost(firstItem.author.userId, firstItem.community.communityId, firstItem.contentId.permlink)
        assertTrue(getPostResp is Either.Success)

        val postsRaw = client.getPostsRaw(type = FeedType.TOP_LIKES,
                allowNsfw = true, timeframe = FeedTimeFrame.MONTH, limit = 100) as Either.Success

        val firstItemRaw = postsRaw.value.items.first()

        val getPostRespRaw = client.getPostRaw(firstItemRaw.author.userId, firstItemRaw.community.communityId, firstItemRaw.contentId.permlink)
        assertTrue(getPostRespRaw is Either.Success)
    }

    @Test
    fun walletTest() {
        val getWalletResult = client.getBalance(CyberName("tst5vmhjuxie"))
        assertTrue(getWalletResult is Either.Success)
        println(getWalletResult)

        val tranferHistoryResponse = client.getTransferHistory(CyberName("tst5vmhjuxie"),
                TransactionDirection.ALL)
        assertTrue(tranferHistoryResponse is Either.Success)
        println(tranferHistoryResponse)

        val getTokens = client.getTokensInfo(getWalletResult.getOrThrow().balances.map { it.symbol })
        assertTrue(getTokens is Either.Success)
        println(getTokens)
    }
}