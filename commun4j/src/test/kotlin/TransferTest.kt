import io.golos.commun4j.Commun4j
import io.golos.commun4j.sharedmodel.CyberName
import io.golos.commun4j.sharedmodel.Either
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TransferTest {
    private lateinit var client: Commun4j
    private lateinit var secondAccount: kotlin.Pair<CyberName, String>

    @Before
    fun before() {
        client = getClient()
        secondAccount = account(client.config.toConfigType())
    }

    @Test
    fun transferSomeMoney() {
        val firstTransferResult =
                client
                        .transfer(secondAccount.first, "0.010", "GOLOS")
        assertTrue("transfer fail", firstTransferResult is Either.Success)
    }
}