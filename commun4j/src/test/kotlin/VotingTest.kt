import io.golos.commun4j.model.BandWidthRequest
import io.golos.commun4j.model.FeedType
import io.golos.commun4j.sharedmodel.CyberSymbolCode
import org.junit.Test
import rx.lang.kotlin.toSingle
import kotlin.random.Random

class VotingTest {
    val client = getClient(authInServices = true)

    @Test
    fun testVoteAndUnvote() {
        val community = client.getPosts(type = FeedType.HOT, limit = 1).getOrThrow().first().community.communityId

        val leader = client.getLeaders(community, limit = 1).getOrThrow().first()

        client.voteLeader(CyberSymbolCode(community), leader.userId,
                1000, BandWidthRequest.bandWidthFromComn).getOrThrow()

        client.unVoteLeader(CyberSymbolCode(community), leader.userId,
                 BandWidthRequest.bandWidthFromComn).getOrThrow()


    }
}