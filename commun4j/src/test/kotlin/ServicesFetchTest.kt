import io.golos.commun4j.model.FeedTimeFrame
import io.golos.commun4j.model.FeedType
import io.golos.commun4j.services.model.*
import io.golos.commun4j.sharedmodel.Either
import io.golos.commun4j.utils.StringSigner
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ServicesFetchTest {
    private val client = getClient(CONFIG_TYPE.DEV, true, false)

    @Test
    fun testAuth() {
        val clintWithUser = getClient(CONFIG_TYPE.DEV, true, false)
        val usr = clintWithUser.keyStorage.getActiveAccount()
        val key = clintWithUser.keyStorage.getActiveAccountKeys().first().second
        val profile = client.getUserProfile(usr, null).getOrThrow()

        val secret = client.getAuthSecret().getOrThrow().secret
        client.authWithSecret(
                profile.username!!,
                secret,
                StringSigner.signString(secret, key)
        ).getOrThrow()
    }


    @Test
    fun getCommunitiesTest() {
        val getCommunitiesResult = client.getCommunitiesList(limit = 1)
        assertTrue(getCommunitiesResult is Either.Success)

        client.getCommunitiesList().getOrThrow()
        client.getCommunitiesList(limit = 1).getOrThrow()
        client.getCommunitiesList(offset = 1).getOrThrow()
        client.getCommunitiesList(search = "CATS").getOrThrow()

        val user = client.getPosts(type = FeedType.TOP_COMMENTS, limit = 1)
                .getOrThrow().items.first().author.userId
        client.getCommunitiesList(type = CommunitiesRequestType.USER, userId = user).getOrThrow()

        val community = (getCommunitiesResult as Either.Success).value.items[0]

        val getComnityResult = client.getCommunity(community.communityId, null)

        assertTrue(getComnityResult is Either.Success)

        val getComnityByAliasResult = client.getCommunity(null, community.alias)

        assertTrue(getComnityByAliasResult is Either.Success)
    }

    @Test
    fun userMetadataFetchTest() {
        val clintWithUser = getClient(CONFIG_TYPE.DEV, true, false)
        val usr = clintWithUser.keyStorage.getActiveAccount()
        val key = clintWithUser.keyStorage.getActiveAccountKeys().first().second

        val profile = client.getUserProfile(usr, null).getOrThrow()

        val response = client
                .getUserProfile(usr, null)
        assertTrue(response is Either.Success)
        println(response)

        val secret = client.getAuthSecret().getOrThrow().secret
        client.authWithSecret(profile.username!!, secret, StringSigner.signString(secret, key)).getOrThrow()

        val posts = client.getPosts(type = FeedType.NEW, timeframe = FeedTimeFrame.MONTH, limit = 20).getOrThrow()
        posts.items.forEach {
            client.getUserProfile(it.author.userId, null)
        }
    }


    @Test
    fun getPost() {
        val posts = client.getPosts(type = FeedType.TOP_LIKES,
                allowNsfw = true, timeframe = FeedTimeFrame.MONTH, limit = 1) as Either.Success
        val firstItem = posts.value.items.first()

        val getPostResp = client.getPost(firstItem.author.userId, firstItem.community.communityId, firstItem.contentId.permlink)
        assertTrue(getPostResp is Either.Success)

        val postsRaw = client.getPostsRaw(type = FeedType.TOP_LIKES,
                allowNsfw = true, timeframe = FeedTimeFrame.MONTH, limit = 100) as Either.Success

        val firstItemRaw = postsRaw.value.items.first()
        println(firstItemRaw.document)
        val getPostRespRaw = client.getPostRaw(firstItemRaw.author.userId, firstItemRaw.community.communityId, firstItemRaw.contentId.permlink)
        assertTrue(getPostRespRaw is Either.Success)
    }

    @Test
    fun getCommentsTest() {
        client.getPosts(type = FeedType.TOP_COMMENTS, limit = 10)
                .getOrThrow()
                .items
                .filter { it.stats?.commentsCount ?: 0 > 0 }
                .forEach {
                    val item = client.getComments(userId = it.contentId.userId, communityId = it.contentId.communityId, permlink = it.contentId.permlink).getOrThrow().items.first()
                    client.getComment(item.contentId.userId, item.contentId.communityId, item.contentId.permlink).getOrThrow()
                    client.getCommentRaw(item.contentId.userId, item.contentId.communityId, item.contentId.permlink).getOrThrow()
                    client.getCommentsRaw(userId = it.contentId.userId, communityId = it.contentId.communityId, permlink = it.contentId.permlink).getOrThrow()
                }
        client.getComments(type = CommentsSortType.USER,
                userId = client.getPosts(type = FeedType.TOP_COMMENTS, limit = 1)
                        .getOrThrow()
                        .items
                        .first()
                        .author
                        .userId)
                .getOrThrow()
    }

    @Test
    fun getPostsTest() {
        val communityId = client.getCommunitiesList().getOrThrow().items.first().communityId
        val res = client.getPosts(type = FeedType.HOT, limit = 1).getOrThrow()
        client.getPosts(type = FeedType.NEW, limit = 1).getOrThrow()
        client.getPosts(type = FeedType.TOP_LIKES, limit = 1).getOrThrow()
        client.getPosts(type = FeedType.TOP_REWARDS, limit = 1).getOrThrow()
        client.getPosts(type = FeedType.TOP_COMMENTS, limit = 1).getOrThrow()
        client.getPosts(type = FeedType.VOTED, limit = 1).getOrThrow()
        client.getPosts(communityId = communityId, type = FeedType.COMMUNITY, limit = 1).getOrThrow()
        client.getPosts(userId = client.keyStorage.getActiveAccount(), type = FeedType.SUBSCRIPTION_HOT, limit = 1).getOrThrow()
        client.getPosts(userId = client.keyStorage.getActiveAccount(), type = FeedType.SUBSCRIPTION_POPULAR, limit = 1).getOrThrow()
    }

    @Test
    fun leadersTest() {
        val getCommunitiesResult = client.getCommunitiesList(limit = 20).getOrThrow()
        getCommunitiesResult.items.forEach {
            client.getLeaders(it.communityId, getRandomNullableInt(), null).getOrThrow()
        }
    }

//    @Test
//    fun getCommunityBlacklistTest() {
//        val getCommunitiesResult = client.getCommunitiesList(0, 25).getOrThrow()
//        getCommunitiesResult.items.forEach {
//            client.getCommunityBlacklist(it.communityId, it.alias, null, getRandomNullableInt()).getOrThrow()
//        }
//    }

    @Test
    fun blacklistTest() {
        val posts = client.getPosts(type = FeedType.TOP_LIKES, timeframe = FeedTimeFrame.MONTH, limit = 20).getOrThrow()
        posts.items.forEach {
            client.getBlacklistedCommunities(it.author.userId).getOrThrow()
            client.getBlacklistedUsers(it.author.userId).getOrThrow()
        }
    }

    @Test
    fun subscribersTest() {
        val posts = client.getPosts(type = FeedType.TOP_LIKES, timeframe = FeedTimeFrame.MONTH, limit = 20).getOrThrow()
        posts.items.forEach {
            client.getSubscribers(it.author.userId, null, getRandomNullableInt(), null).getOrThrow()
            client.getSubscribers(null, it.contentId.communityId, getRandomNullableInt(), null).getOrThrow()
        }
    }

    @Test
    fun subscriptionsTest() {
        val posts = client.getPosts(type = FeedType.TOP_LIKES, timeframe = FeedTimeFrame.MONTH, limit = 20).getOrThrow()
        posts.items.forEach {
            client.getUserSubscriptions(it.author.userId, null, getRandomNullableInt()).getOrThrow()
            client.getCommunitySubscriptions(it.author.userId, getRandomNullableInt(), getRandomNullableInt()).getOrThrow()
        }
    }

    @Test
    fun getReportsTest() {
        val communitites = client.getCommunitiesList(null, limit = 100).getOrThrow().items.map { it.communityId }
        client.getReports(communitites, null, ReportRequestContentType.POST, null, 5, 0).getOrThrow()
        client.getReports(communitites, ReportsRequestStatus.OPEN, ReportRequestContentType.COMMENT, null, 5, 0).getOrThrow()
        client.getReports(communitites, ReportsRequestStatus.CLOSED, ReportRequestContentType.POST, null, 5, 0).getOrThrow()
        client.getReports(communitites, null, ReportRequestContentType.COMMENT, ReportsRequestTimeSort.REPORTS_COUNT, 5, 0).getOrThrow()
        client.getReports(communitites, null, ReportRequestContentType.POST, ReportsRequestTimeSort.TIME, 5, 0).getOrThrow()
        client.getReports(communitites, null, ReportRequestContentType.COMMENT, ReportsRequestTimeSort.TIME_DESC, 5, 0).getOrThrow()
    }

    @Test
    fun suggestNames() {
        val name = client.getPosts(type = FeedType.TOP_LIKES, timeframe = FeedTimeFrame.MONTH, limit = 20).getOrThrow().items
                .map { it.author.username?.substring(0..3) ?: "unknown" }
        name.forEach {
            client.suggestNames(it).getOrThrow()
        }
    }

    @Test
    fun stateBulkTest() {
        val postsData = client.getPosts(type = FeedType.TOP_LIKES).getOrThrow().map { UserAndPermlinkPair(it.author.userId, it.contentId.permlink) }
        val result = client.getStateBulk(
                postsData
        )
        result.getOrThrow().forEach { it.toPair().second.forEach { it.collectionEnd } }
    }

    private fun getRandomNullableInt() = if (Random.nextDouble() > 0.5) (100 * Random.nextDouble()).toInt().let {
        if (it == 0) 1 else it
    } else null

    @Test
    fun searchTest() {
        val quickSearch = client.quickSearch("s", 500, listOf(SearchableEntities.POSTS, SearchableEntities.PROFILES, SearchableEntities.COMMUNITIES)).getOrThrow()

        val extendedSearchRequestSearch = client.extendedSearch("s",
                ExtendedRequestSearchItem(100, 0),
                ExtendedRequestSearchItem(100, 0),
                ExtendedRequestSearchItem(100, 0)).getOrThrow()
        print(quickSearch)
    }


}