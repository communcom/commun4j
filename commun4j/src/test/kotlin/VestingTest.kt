//import io.golos.commun4j.sharedmodel.Either
//import junit.framework.Assert.assertTrue
//import org.junit.Test
//
//class VestingTest {
//    private val client = getClient()
//
//    @Test
//    fun testVoteAndUnvote() {
//        val client = getClient()
//
//        val newAccount = AccountCreationTest.createNewAccount(client.config)
//        client.setActiveAccount(newAccount)
//
//        val createWitnessResult = client.registerAWitness("url")
//
//        assertTrue(createWitnessResult is Either.Success)
//
//        val voteResult = client.voteForAWitness(client.activeAccountPair.first)
//
//        assertTrue(voteResult is Either.Success)
//
//        val unvoteResult = client.unVoteForAWitness(client.activeAccountPair.first)
//
//        assertTrue(unvoteResult is Either.Success)
//
//        Thread.sleep(1_000)
//
//        val unregisterWitnessResult = client.unRegisterWitness()
//
//        assertTrue(unregisterWitnessResult is Either.Success)
//
//    }
//
//    @Test
//    fun testDelegate() {
//
//        val second = account(CONFIG_TYPE.DEV)
//
//        val result = client.withdraw(
//                client.activeAccountPair.first, second.first, "0.100000 GOLOS", client.activeAccountPair.second
//        )
//
//        assertTrue(result is Either.Success)
//
//        val secondResult = client.stopWithdraw(client.activeAccountPair.first,
//                client.activeAccountPair.second)
//
//        assertTrue(secondResult is Either.Success)
//
//    }
//}