import io.golos.commun4j.services.model.ContentParsingType
import io.golos.commun4j.services.model.FeedSort
import io.golos.commun4j.services.model.FeedTimeFrame
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.Either
import org.junit.Assert.assertTrue
import org.junit.Test

class ServicesFetchTest {
    private val client = getClient(CONFIG_TYPE.DEV, false, false)

    @Test
    fun fetchPostsTest() {
        val postsResponse = client.getCommunityPosts(
                "gls",
                ContentParsingType.MOBILE,
                FeedTimeFrame.ALL,
                100,
                FeedSort.INVERTED,
                null)

        assertTrue(postsResponse is Either.Success)

        val posts = (postsResponse as Either.Success).value.items

        assertTrue("posts feed is empty", posts.isNotEmpty())

        val post = posts[4]

        val postResponse = client.getPost(
                post.contentId.userId.toCyberName(),
                null,
                post.contentId.permlink,
                ContentParsingType.MOBILE)

        assertTrue(postResponse is Either.Success)

        val comments = client.getCommentsOfPost(
                post.author!!.userId.name.toCyberName(),
                null,
                post.contentId.permlink,
                ContentParsingType.RAW,
                100,
                FeedSort.INVERTED)

        assertTrue(comments is Either.Success)

        val commentsOfUser = client.getCommentsOfUser(
                post.author!!.userId.name.toCyberName(),
                null,
                ContentParsingType.MOBILE,
                10,
                FeedSort.INVERTED,
                null
        )

        assertTrue(commentsOfUser is Either.Success)

        val subscriptionsOfUser = client.getUserSubscriptions(
                post.author!!.userId.name.toCyberName(),
                null,
                ContentParsingType.MOBILE,
                100,
                FeedSort.INVERTED,
                null
        )

        assertTrue(subscriptionsOfUser is Either.Success)

        val postsOfUser = client.getUserPosts(
                post.author!!.userId.name.toCyberName(),
                null,
                ContentParsingType.MOBILE,
                100,
                FeedSort.INVERTED,
                null
        )

        assertTrue(postsOfUser is Either.Success)

    }

    @Test
    fun getCommentTest() {
        val postsResponse = client.getCommunityPosts(
                "gls",
                ContentParsingType.MOBILE,
                FeedTimeFrame.MONTH,
                20,
                FeedSort.INVERTED,
                null
        )

        assertTrue(postsResponse is Either.Success)

        val posts = (postsResponse as Either.Success).value.items

        val postWithComments = posts.find { it.stats?.commentsCount ?: 0 > 0 }!!

        val comments = client.getCommentsOfPost(
                postWithComments.contentId.userId.toCyberName(),
                null,
                postWithComments.contentId.permlink,
                ContentParsingType.MOBILE,
                1, FeedSort.SEQUENTIALLY, null
        )

        assertTrue(comments is Either.Success)

        val comment = (comments as Either.Success).value.items.first()

        val fetchedComment = client.getComment(
                comment.contentId.userId.toCyberName(),
                null,
                comment.contentId.permlink,
                ContentParsingType.MOBILE
        )

        assertTrue(fetchedComment is Either.Success)

        val comments1 = client
                .getUserReplies(postWithComments.contentId.userId.toCyberName(), null,
                        ContentParsingType.WEB, 10, FeedSort.INVERTED, null)
        assertTrue(comments1 is Either.Success)

    }



    @Test
    fun userProfileTest() {
        val response = client.getUserAccount(
                (client.resolveCanonicalCyberName("joseph.kalu", "gls") as Either.Success).value.userId)
        assertTrue(response is Either.Success)
    }

    @Test
    fun getEmbedTest() {
        val response =
                client.getEmbedIframely("https://music.yandex.ru/album/3100408/track/3869231")
        assertTrue(response is Either.Success)

    }


    @Test
    fun testSubscriptionsAndSubscribers() {
        val userId = (client.resolveCanonicalCyberName("ehhehehehe", "gls") as Either.Success).value.userId

        val subscriptionsToUsers =
                client.getSubscriptionsToUsers(userId, 10, null)
        assertTrue(subscriptionsToUsers is Either.Success)

        val subscriptionsToCommunitites =
                client.getSubscriptionsToCommunities(userId, 10, null)

        assertTrue(subscriptionsToCommunitites is Either.Success)

        val subscribedUsers = client.getUsersSubscribedToUser(userId, 10, null)

        assertTrue(subscribedUsers is Either.Success)

        val subscribedCommunitites =
                client.getCommunitiesSubscribedToUser(userId, 10, null)

        assertTrue(subscribedCommunitites is Either.Success)
    }

    @Test
    fun getCommunititeTest(){
        val getCommunitiesResult = client.getCommunitiesList(CyberName("lol"), 1)
        assertTrue(getCommunitiesResult is Either.Success)

        val community = (getCommunitiesResult as Either.Success).value.communities[0]

        val getComnityResult = client.getCommunity(community.communityId, CyberName("lol"))

        assertTrue(getComnityResult is Either.Success)

    }

    @Test
    fun userMetadataFetchTest() {
        val response = client
                .getUserProfile(CyberName("lol"), null)
        assertTrue(response is Either.Success)
    }
}